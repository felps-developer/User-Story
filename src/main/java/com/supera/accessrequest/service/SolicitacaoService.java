package com.supera.accessrequest.service;

import com.supera.accessrequest.dto.CancelarSolicitacaoRequest;
import com.supera.accessrequest.dto.SolicitacaoRequest;
import com.supera.accessrequest.dto.SolicitacaoResponse;
import com.supera.accessrequest.entity.*;
import com.supera.accessrequest.enums.Departamento;
import com.supera.accessrequest.enums.StatusSolicitacao;
import com.supera.accessrequest.exception.BusinessException;
import com.supera.accessrequest.exception.ResourceNotFoundException;
import com.supera.accessrequest.exception.UnauthorizedException;
import com.supera.accessrequest.repository.*;
import com.supera.accessrequest.util.ProtocoloGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModuloRepository moduloRepository;
    private final ModuloDepartamentoRepository moduloDepartamentoRepository;
    private final ModuloIncompativelRepository moduloIncompativelRepository;
    private final AcessoUsuarioModuloRepository acessoUsuarioModuloRepository;
    private final HistoricoSolicitacaoRepository historicoRepository;
    private final ProtocoloGenerator protocoloGenerator;
    private final AuthService authService;

    private static final List<String> JUSTIFICATIVAS_GENERICAS = Arrays.asList(
            "teste", "test", "aaa", "bbb", "preciso", "necessário", "necessario",
            "quero", "want", "need", "xxx", "123", "abc"
    );

    private static final int DIAS_PARA_EXPIRACAO = 180;
    private static final int DIAS_PARA_RENOVACAO = 30;

    @Transactional
    public SolicitacaoResponse criarSolicitacao(SolicitacaoRequest request) {
        Long usuarioId = authService.getUsuarioLogadoId();
        log.info("Criando solicitação para usuário ID: {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", usuarioId));

        // Validações
        validarJustificativa(request.justificativa());
        List<Modulo> modulos = moduloRepository.findAllByIdsAndAtivoTrue(request.modulosIds());
        validarModulosAtivos(modulos, request.modulosIds());
        validarSolicitacoesAtivas(usuario.getId(), request.modulosIds());
        validarAcessosAtivos(usuario.getId(), request.modulosIds());

        // Criar solicitação
        Solicitacao solicitacao = criarSolicitacaoEntity(usuario, request);

        // Adicionar módulos
        modulos.forEach(solicitacao::addModulo);

        // Validar regras de negócio e aprovar/negar
        String resultadoValidacao = validarRegrasNegocio(usuario, modulos);

        if (resultadoValidacao == null) {
            // Aprovado
            solicitacao.setStatus(StatusSolicitacao.ATIVO);
            solicitacao.setDataExpiracao(LocalDateTime.now().plusDays(DIAS_PARA_EXPIRACAO));
            solicitacao = solicitacaoRepository.save(solicitacao);

            // Conceder acessos
            concederAcessos(usuario, modulos, solicitacao);

            // Histórico
            solicitacao.addHistorico("CRIADA_E_APROVADA", "Solicitação criada e aprovada automaticamente", usuario);
            
            log.info("Solicitação {} APROVADA para usuário {}", solicitacao.getProtocolo(), usuario.getEmail());
            
        } else {
            // Negado
            solicitacao.setStatus(StatusSolicitacao.NEGADO);
            solicitacao.setMotivoNegacao(resultadoValidacao);
            solicitacao = solicitacaoRepository.save(solicitacao);

            // Histórico
            solicitacao.addHistorico("CRIADA_E_NEGADA", 
                    "Solicitação criada e negada automaticamente: " + resultadoValidacao, usuario);
            
            log.warn("Solicitação {} NEGADA para usuário {}: {}", 
                    solicitacao.getProtocolo(), usuario.getEmail(), resultadoValidacao);
        }

        solicitacao = solicitacaoRepository.save(solicitacao);
        return SolicitacaoResponse.fromEntity(solicitacao);
    }

    @Transactional(readOnly = true)
    public Page<SolicitacaoResponse> listarMinhasSolicitacoes(
            StatusSolicitacao status,
            Boolean urgente,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            String pesquisa,
            int page,
            int size
    ) {
        Long usuarioId = authService.getUsuarioLogadoId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataSolicitacao"));
        
        Page<Solicitacao> solicitacoes = solicitacaoRepository.findByUsuarioIdWithFilters(
                usuarioId, status, urgente, dataInicio, dataFim, pesquisa, pageable
        );

        return solicitacoes.map(SolicitacaoResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public SolicitacaoResponse buscarSolicitacaoPorId(Long id) {
        Long usuarioId = authService.getUsuarioLogadoId();
        
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação", "id", id));

        // Validar que o usuário só pode ver suas próprias solicitações
        if (!solicitacao.getUsuario().getId().equals(usuarioId)) {
            throw new UnauthorizedException("Você não tem permissão para visualizar esta solicitação");
        }

        return SolicitacaoResponse.fromEntity(solicitacao);
    }

    @Transactional
    public SolicitacaoResponse renovarSolicitacao(Long id) {
        Long usuarioId = authService.getUsuarioLogadoId();
        
        Solicitacao solicitacaoOriginal = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação", "id", id));

        // Validações de renovação
        if (!solicitacaoOriginal.getUsuario().getId().equals(usuarioId)) {
            throw new UnauthorizedException("Você não tem permissão para renovar esta solicitação");
        }

        if (solicitacaoOriginal.getStatus() != StatusSolicitacao.ATIVO) {
            throw new BusinessException("Apenas solicitações ativas podem ser renovadas");
        }

        LocalDateTime dataLimiteRenovacao = LocalDateTime.now().plusDays(DIAS_PARA_RENOVACAO);
        if (solicitacaoOriginal.getDataExpiracao().isAfter(dataLimiteRenovacao)) {
            throw new BusinessException("Renovação permitida apenas quando faltarem menos de 30 dias para expiração");
        }

        // Criar nova solicitação baseada na original
        List<Long> modulosIds = solicitacaoOriginal.getModulos().stream()
                .map(sm -> sm.getModulo().getId())
                .collect(Collectors.toList());

        SolicitacaoRequest renovacaoRequest = new SolicitacaoRequest(
                modulosIds,
                "Renovação da solicitação " + solicitacaoOriginal.getProtocolo(),
                false
        );

        // Criar nova solicitação
        Solicitacao novaSolicitacao = criarSolicitacaoEntity(solicitacaoOriginal.getUsuario(), renovacaoRequest);
        novaSolicitacao.setSolicitacaoOrigem(solicitacaoOriginal);

        // Adicionar módulos
        solicitacaoOriginal.getModulos().forEach(sm -> novaSolicitacao.addModulo(sm.getModulo()));

        // Reaplicar regras de negócio
        List<Modulo> modulos = solicitacaoOriginal.getModulos().stream()
                .map(SolicitacaoModulo::getModulo)
                .collect(Collectors.toList());

        String resultadoValidacao = validarRegrasNegocio(solicitacaoOriginal.getUsuario(), modulos);

        if (resultadoValidacao == null) {
            // Aprovado - Estender validade dos acessos existentes
            novaSolicitacao.setStatus(StatusSolicitacao.ATIVO);
            novaSolicitacao.setDataExpiracao(LocalDateTime.now().plusDays(DIAS_PARA_EXPIRACAO));
            novaSolicitacao = solicitacaoRepository.save(novaSolicitacao);

            // Atualizar data de expiração dos acessos
            estenderAcessos(solicitacaoOriginal.getUsuario(), modulos, novaSolicitacao);

            novaSolicitacao.addHistorico("RENOVADA", 
                    "Renovação da solicitação " + solicitacaoOriginal.getProtocolo(), 
                    solicitacaoOriginal.getUsuario());
            
            log.info("Solicitação {} renovada com sucesso. Nova solicitação: {}", 
                    solicitacaoOriginal.getProtocolo(), novaSolicitacao.getProtocolo());
        } else {
            // Negado
            novaSolicitacao.setStatus(StatusSolicitacao.NEGADO);
            novaSolicitacao.setMotivoNegacao(resultadoValidacao);
            novaSolicitacao = solicitacaoRepository.save(novaSolicitacao);

            novaSolicitacao.addHistorico("RENOVACAO_NEGADA", 
                    "Renovação negada: " + resultadoValidacao, 
                    solicitacaoOriginal.getUsuario());
            
            log.warn("Renovação da solicitação {} negada: {}", 
                    solicitacaoOriginal.getProtocolo(), resultadoValidacao);
        }

        return SolicitacaoResponse.fromEntity(solicitacaoRepository.save(novaSolicitacao));
    }

    @Transactional
    public void cancelarSolicitacao(Long id, CancelarSolicitacaoRequest request) {
        Long usuarioId = authService.getUsuarioLogadoId();
        
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação", "id", id));

        // Validações
        if (!solicitacao.getUsuario().getId().equals(usuarioId)) {
            throw new UnauthorizedException("Você não tem permissão para cancelar esta solicitação");
        }

        if (solicitacao.getStatus() != StatusSolicitacao.ATIVO) {
            throw new BusinessException("Apenas solicitações ativas podem ser canceladas");
        }

        // Cancelar solicitação
        solicitacao.setStatus(StatusSolicitacao.CANCELADO);
        solicitacao.setMotivoCancelamento(request.motivo());

        // Revogar acessos
        acessoUsuarioModuloRepository.desativarAcessosPorSolicitacaoId(id);

        // Histórico
        solicitacao.addHistorico("CANCELADA", 
                "Solicitação cancelada pelo usuário: " + request.motivo(), 
                solicitacao.getUsuario());

        solicitacaoRepository.save(solicitacao);
        
        log.info("Solicitação {} cancelada pelo usuário {}", 
                solicitacao.getProtocolo(), solicitacao.getUsuario().getEmail());
    }

    // ==================== Métodos Auxiliares ====================

    private Solicitacao criarSolicitacaoEntity(Usuario usuario, SolicitacaoRequest request) {
        long sequenciaHoje = solicitacaoRepository.countSolicitacoesHoje();
        String protocolo = protocoloGenerator.generate(sequenciaHoje);

        return Solicitacao.builder()
                .protocolo(protocolo)
                .usuario(usuario)
                .justificativa(request.justificativa())
                .urgente(request.urgente())
                .dataSolicitacao(LocalDateTime.now())
                .modulos(new HashSet<>())
                .historico(new HashSet<>())
                .build();
    }

    private void validarJustificativa(String justificativa) {
        String justificativaLower = justificativa.toLowerCase().trim();
        
        for (String termo : JUSTIFICATIVAS_GENERICAS) {
            if (justificativaLower.equals(termo) || justificativaLower.contains(termo + " ")) {
                throw new BusinessException("Justificativa insuficiente ou genérica");
            }
        }
    }

    private void validarModulosAtivos(List<Modulo> modulos, List<Long> idsRequisitados) {
        if (modulos.size() != idsRequisitados.size()) {
            throw new BusinessException("Um ou mais módulos não foram encontrados ou estão inativos");
        }
    }

    private void validarSolicitacoesAtivas(Long usuarioId, List<Long> modulosIds) {
        for (Long moduloId : modulosIds) {
            boolean temSolicitacaoAtiva = solicitacaoRepository
                    .existsByUsuarioIdAndModuloIdAndStatus(usuarioId, moduloId, StatusSolicitacao.ATIVO);
            
            if (temSolicitacaoAtiva) {
                throw new BusinessException("Você já possui uma solicitação ativa para um dos módulos selecionados");
            }
        }
    }

    private void validarAcessosAtivos(Long usuarioId, List<Long> modulosIds) {
        for (Long moduloId : modulosIds) {
            boolean temAcessoAtivo = acessoUsuarioModuloRepository
                    .existsByUsuarioIdAndModuloIdAndAtivoTrue(usuarioId, moduloId);
            
            if (temAcessoAtivo) {
                throw new BusinessException("Você já possui acesso ativo para um dos módulos selecionados");
            }
        }
    }

    private String validarRegrasNegocio(Usuario usuario, List<Modulo> modulos) {
        // 1. Validar compatibilidade de departamento
        for (Modulo modulo : modulos) {
            if (!isModuloCompativelComDepartamento(modulo.getId(), usuario.getDepartamento())) {
                return "Departamento sem permissão para acessar este módulo: " + modulo.getNome();
            }
        }

        // 2. Validar módulos incompatíveis
        List<Long> modulosAtivosUsuario = acessoUsuarioModuloRepository
                .findModuloIdsAtivosByUsuarioId(usuario.getId());
        
        for (Modulo modulo : modulos) {
            List<Long> incompatibilidades = moduloIncompativelRepository
                    .findModulosIncompatibilidadesByModuloId(modulo.getId());
            
            for (Long moduloAtivoId : modulosAtivosUsuario) {
                if (incompatibilidades.contains(moduloAtivoId)) {
                    return "Módulo incompatível com outro módulo já ativo em seu perfil";
                }
            }
            
            // Verificar incompatibilidade entre os módulos sendo solicitados
            for (Modulo outroModulo : modulos) {
                if (!modulo.getId().equals(outroModulo.getId()) && 
                    incompatibilidades.contains(outroModulo.getId())) {
                    return "Não é permitido solicitar módulos incompatíveis entre si";
                }
            }
        }

        // 3. Validar limite de módulos
        long modulosAtivos = acessoUsuarioModuloRepository.countByUsuarioIdAndAtivoTrue(usuario.getId());
        int limiteModulos = usuario.getDepartamento() == Departamento.TI ? 10 : 5;
        
        if ((modulosAtivos + modulos.size()) > limiteModulos) {
            return "Limite de módulos ativos atingido";
        }

        return null; // Aprovado
    }

    private boolean isModuloCompativelComDepartamento(Long moduloId, Departamento departamento) {
        // TI pode acessar todos os módulos
        if (departamento == Departamento.TI) {
            return true;
        }

        return moduloDepartamentoRepository
                .existsByModuloIdAndDepartamento(moduloId, departamento);
    }

    private void concederAcessos(Usuario usuario, List<Modulo> modulos, Solicitacao solicitacao) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime expiracao = agora.plusDays(DIAS_PARA_EXPIRACAO);

        for (Modulo modulo : modulos) {
            AcessoUsuarioModulo acesso = AcessoUsuarioModulo.builder()
                    .usuario(usuario)
                    .modulo(modulo)
                    .solicitacao(solicitacao)
                    .dataInicio(agora)
                    .dataExpiracao(expiracao)
                    .ativo(true)
                    .build();
            
            acessoUsuarioModuloRepository.save(acesso);
        }

        log.info("Acessos concedidos para usuário {} - {} módulos", usuario.getEmail(), modulos.size());
    }

    private void estenderAcessos(Usuario usuario, List<Modulo> modulos, Solicitacao solicitacao) {
        LocalDateTime novaExpiracao = LocalDateTime.now().plusDays(DIAS_PARA_EXPIRACAO);

        List<AcessoUsuarioModulo> acessosAtuais = acessoUsuarioModuloRepository
                .findByUsuarioIdAndAtivoTrue(usuario.getId());

        for (AcessoUsuarioModulo acesso : acessosAtuais) {
            if (modulos.stream().anyMatch(m -> m.getId().equals(acesso.getModulo().getId()))) {
                acesso.setDataExpiracao(novaExpiracao);
                acessoUsuarioModuloRepository.save(acesso);
            }
        }

        log.info("Acessos estendidos para usuário {} até {}", usuario.getEmail(), novaExpiracao);
    }
}


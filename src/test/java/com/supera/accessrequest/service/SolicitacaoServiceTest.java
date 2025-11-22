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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SolicitacaoService - Testes Unitários")
class SolicitacaoServiceTest {

    @Mock
    private SolicitacaoRepository solicitacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ModuloRepository moduloRepository;

    @Mock
    private ModuloDepartamentoRepository moduloDepartamentoRepository;

    @Mock
    private ModuloIncompativelRepository moduloIncompativelRepository;

    @Mock
    private AcessoUsuarioModuloRepository acessoUsuarioModuloRepository;

    @Mock
    private HistoricoSolicitacaoRepository historicoRepository;

    @Mock
    private ProtocoloGenerator protocoloGenerator;

    @Mock
    private AuthService authService;

    @InjectMocks
    private SolicitacaoService solicitacaoService;

    private Usuario usuario;
    private Modulo modulo1;
    private Modulo modulo2;
    private Solicitacao solicitacao;
    private SolicitacaoRequest solicitacaoRequest;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao.silva@empresa.com")
                .senha("$2a$12$encoded_password")
                .departamento(Departamento.FINANCEIRO)
                .ativo(true)
                .build();

        modulo1 = Modulo.builder()
                .id(1L)
                .nome("CRM")
                .descricao("Sistema de gestão de clientes")
                .ativo(true)
                .build();

        modulo2 = Modulo.builder()
                .id(2L)
                .nome("Financeiro")
                .descricao("Sistema financeiro")
                .ativo(true)
                .build();

        solicitacaoRequest = new SolicitacaoRequest(
                Arrays.asList(1L, 2L),
                "Acesso solicitado para implementação do projeto Phoenix junto ao cliente corporativo conforme demanda do gestor",
                false
        );

        solicitacao = Solicitacao.builder()
                .id(1L)
                .protocolo("SOL-20231122-0001")
                .usuario(usuario)
                .justificativa(solicitacaoRequest.justificativa())
                .urgente(false)
                .status(StatusSolicitacao.ATIVO)
                .dataSolicitacao(LocalDateTime.now())
                .dataExpiracao(LocalDateTime.now().plusDays(180))
                .modulos(new HashSet<>())
                .historico(new HashSet<>())
                .build();
    }

    @Test
    @DisplayName("Deve criar solicitação e aprovar automaticamente quando regras são atendidas")
    void deveCriarSolicitacaoEAprovarAutomaticamenteQuandoRegrasAtendidas() {
        // Arrange
        List<Modulo> modulos = Arrays.asList(modulo1, modulo2);
        
        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(usuarioRepository.findById(eq(1L))).thenReturn(Optional.of(usuario));
        when(moduloRepository.findAllByIdsAndAtivoTrue(eq(solicitacaoRequest.modulosIds()))).thenReturn(modulos);
        when(solicitacaoRepository.existsByUsuarioIdAndModuloIdAndStatus(eq(1L), eq(1L), eq(StatusSolicitacao.ATIVO))).thenReturn(false);
        when(solicitacaoRepository.existsByUsuarioIdAndModuloIdAndStatus(eq(1L), eq(2L), eq(StatusSolicitacao.ATIVO))).thenReturn(false);
        when(acessoUsuarioModuloRepository.existsByUsuarioIdAndModuloIdAndAtivoTrue(eq(1L), eq(1L))).thenReturn(false);
        when(acessoUsuarioModuloRepository.existsByUsuarioIdAndModuloIdAndAtivoTrue(eq(1L), eq(2L))).thenReturn(false);
        when(protocoloGenerator.generate(eq(0L))).thenReturn("SOL-20231122-0001");
        when(solicitacaoRepository.countSolicitacoesHoje()).thenReturn(0L);
        when(moduloDepartamentoRepository.existsByModuloIdAndDepartamento(eq(1L), eq(Departamento.FINANCEIRO))).thenReturn(true);
        when(moduloDepartamentoRepository.existsByModuloIdAndDepartamento(eq(2L), eq(Departamento.FINANCEIRO))).thenReturn(true);
        when(acessoUsuarioModuloRepository.findModuloIdsAtivosByUsuarioId(eq(1L))).thenReturn(Arrays.asList());
        when(moduloIncompativelRepository.findModulosIncompatibilidadesByModuloId(eq(1L))).thenReturn(Arrays.asList());
        when(moduloIncompativelRepository.findModulosIncompatibilidadesByModuloId(eq(2L))).thenReturn(Arrays.asList());
        when(acessoUsuarioModuloRepository.countByUsuarioIdAndAtivoTrue(eq(1L))).thenReturn(0L);
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(solicitacao);

        // Act
        SolicitacaoResponse resultado = solicitacaoService.criarSolicitacao(solicitacaoRequest);

        // Assert
        assertNotNull(resultado);
        assertEquals("SOL-20231122-0001", resultado.protocolo());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(usuarioRepository).findById(eq(1L));
        verify(moduloRepository).findAllByIdsAndAtivoTrue(eq(solicitacaoRequest.modulosIds()));
        verify(solicitacaoRepository, atLeast(1)).save(any(Solicitacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando justificativa é genérica")
    void deveLancarExcecaoQuandoJustificativaGenerica() {
        // Arrange
        SolicitacaoRequest requestInvalido = new SolicitacaoRequest(
                Arrays.asList(1L),
                "teste",
                false
        );

        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(usuarioRepository.findById(eq(1L))).thenReturn(Optional.of(usuario));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> solicitacaoService.criarSolicitacao(requestInvalido));
        assertEquals("Justificativa insuficiente ou genérica", exception.getMessage());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(usuarioRepository).findById(eq(1L));
        verify(moduloRepository, never()).findAllByIdsAndAtivoTrue(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando módulo não existe ou está inativo")
    void deveLancarExcecaoQuandoModuloNaoExisteOuInativo() {
        // Arrange
        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(usuarioRepository.findById(eq(1L))).thenReturn(Optional.of(usuario));
        when(moduloRepository.findAllByIdsAndAtivoTrue(eq(solicitacaoRequest.modulosIds())))
                .thenReturn(Arrays.asList(modulo1)); // Apenas 1 módulo encontrado, mas 2 foram solicitados

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> solicitacaoService.criarSolicitacao(solicitacaoRequest));
        assertEquals("Um ou mais módulos não foram encontrados ou estão inativos", exception.getMessage());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(usuarioRepository).findById(eq(1L));
        verify(moduloRepository).findAllByIdsAndAtivoTrue(eq(solicitacaoRequest.modulosIds()));
    }

    @Test
    @DisplayName("Deve lançar exceção quando já existe solicitação ativa para o módulo")
    void deveLancarExcecaoQuandoJaExisteSolicitacaoAtivaParaModulo() {
        // Arrange
        List<Modulo> modulos = Arrays.asList(modulo1, modulo2);

        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(usuarioRepository.findById(eq(1L))).thenReturn(Optional.of(usuario));
        when(moduloRepository.findAllByIdsAndAtivoTrue(eq(solicitacaoRequest.modulosIds()))).thenReturn(modulos);
        when(solicitacaoRepository.existsByUsuarioIdAndModuloIdAndStatus(eq(1L), eq(1L), eq(StatusSolicitacao.ATIVO))).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> solicitacaoService.criarSolicitacao(solicitacaoRequest));
        assertEquals("Você já possui uma solicitação ativa para um dos módulos selecionados", exception.getMessage());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(usuarioRepository).findById(eq(1L));
        verify(moduloRepository).findAllByIdsAndAtivoTrue(eq(solicitacaoRequest.modulosIds()));
        verify(solicitacaoRepository).existsByUsuarioIdAndModuloIdAndStatus(eq(1L), eq(1L), eq(StatusSolicitacao.ATIVO));
    }

    @Test
    @DisplayName("Deve lançar exceção quando já existe acesso ativo para o módulo")
    void deveLancarExcecaoQuandoJaExisteAcessoAtivoParaModulo() {
        // Arrange
        List<Modulo> modulos = Arrays.asList(modulo1, modulo2);

        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(usuarioRepository.findById(eq(1L))).thenReturn(Optional.of(usuario));
        when(moduloRepository.findAllByIdsAndAtivoTrue(eq(solicitacaoRequest.modulosIds()))).thenReturn(modulos);
        when(solicitacaoRepository.existsByUsuarioIdAndModuloIdAndStatus(eq(1L), eq(1L), eq(StatusSolicitacao.ATIVO))).thenReturn(false);
        when(solicitacaoRepository.existsByUsuarioIdAndModuloIdAndStatus(eq(1L), eq(2L), eq(StatusSolicitacao.ATIVO))).thenReturn(false);
        when(acessoUsuarioModuloRepository.existsByUsuarioIdAndModuloIdAndAtivoTrue(eq(1L), eq(1L))).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> solicitacaoService.criarSolicitacao(solicitacaoRequest));
        assertEquals("Você já possui acesso ativo para um dos módulos selecionados", exception.getMessage());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(usuarioRepository).findById(eq(1L));
        verify(moduloRepository).findAllByIdsAndAtivoTrue(eq(solicitacaoRequest.modulosIds()));
        verify(acessoUsuarioModuloRepository).existsByUsuarioIdAndModuloIdAndAtivoTrue(eq(1L), eq(1L));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Arrange
        when(authService.getUsuarioLogadoId()).thenReturn(999L);
        when(usuarioRepository.findById(eq(999L))).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> solicitacaoService.criarSolicitacao(solicitacaoRequest));
        assertTrue(exception.getMessage().contains("Usuário"));

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(usuarioRepository).findById(eq(999L));
    }

    @Test
    @DisplayName("Deve buscar solicitação por ID com sucesso")
    void deveBuscarSolicitacaoPorIdComSucesso() {
        // Arrange
        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(solicitacao));

        // Act
        SolicitacaoResponse resultado = solicitacaoService.buscarSolicitacaoPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals("SOL-20231122-0001", resultado.protocolo());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(solicitacaoRepository).findById(eq(1L));
    }

    @Test
    @DisplayName("Deve lançar exceção quando solicitação não encontrada")
    void deveLancarExcecaoQuandoSolicitacaoNaoEncontrada() {
        // Arrange
        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(solicitacaoRepository.findById(eq(999L))).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> solicitacaoService.buscarSolicitacaoPorId(999L));
        assertTrue(exception.getMessage().contains("Solicitação"));

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(solicitacaoRepository).findById(eq(999L));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário tenta acessar solicitação de outro usuário")
    void deveLancarExcecaoQuandoUsuarioTentaAcessarSolicitacaoDeOutroUsuario() {
        // Arrange
        when(authService.getUsuarioLogadoId()).thenReturn(2L); // Usuário diferente
        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(solicitacao));

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> solicitacaoService.buscarSolicitacaoPorId(1L));
        assertEquals("Você não tem permissão para visualizar esta solicitação", exception.getMessage());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(solicitacaoRepository).findById(eq(1L));
    }

    @Test
    @DisplayName("Deve cancelar solicitação com sucesso")
    void deveCancelarSolicitacaoComSucesso() {
        // Arrange
        CancelarSolicitacaoRequest cancelarRequest = new CancelarSolicitacaoRequest(
                "Não preciso mais do acesso"
        );

        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(solicitacao));
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(solicitacao);

        // Act
        solicitacaoService.cancelarSolicitacao(1L, cancelarRequest);

        // Assert & Verify
        verify(authService).getUsuarioLogadoId();
        verify(solicitacaoRepository).findById(eq(1L));
        verify(acessoUsuarioModuloRepository).desativarAcessosPorSolicitacaoId(eq(1L));
        verify(solicitacaoRepository).save(any(Solicitacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cancelar solicitação de outro usuário")
    void deveLancarExcecaoAoTentarCancelarSolicitacaoDeOutroUsuario() {
        // Arrange
        CancelarSolicitacaoRequest cancelarRequest = new CancelarSolicitacaoRequest(
                "Não preciso mais do acesso"
        );

        when(authService.getUsuarioLogadoId()).thenReturn(2L); // Usuário diferente
        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(solicitacao));

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> solicitacaoService.cancelarSolicitacao(1L, cancelarRequest));
        assertEquals("Você não tem permissão para cancelar esta solicitação", exception.getMessage());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(solicitacaoRepository).findById(eq(1L));
        verify(acessoUsuarioModuloRepository, never()).desativarAcessosPorSolicitacaoId(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cancelar solicitação não ativa")
    void deveLancarExcecaoAoTentarCancelarSolicitacaoNaoAtiva() {
        // Arrange
        solicitacao.setStatus(StatusSolicitacao.CANCELADO);
        CancelarSolicitacaoRequest cancelarRequest = new CancelarSolicitacaoRequest(
                "Não preciso mais do acesso"
        );

        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(solicitacao));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> solicitacaoService.cancelarSolicitacao(1L, cancelarRequest));
        assertEquals("Apenas solicitações ativas podem ser canceladas", exception.getMessage());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(solicitacaoRepository).findById(eq(1L));
        verify(acessoUsuarioModuloRepository, never()).desativarAcessosPorSolicitacaoId(any());
    }

    @Test
    @DisplayName("Deve listar solicitações do usuário com sucesso")
    void deveListarSolicitacoesDoUsuarioComSucesso() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(Arrays.asList(solicitacao), pageable, 1);

        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(solicitacaoRepository.findByUsuarioIdWithFilters(
                eq(1L), eq(null), eq(null), eq(null), eq(null), eq(null), any(Pageable.class)
        )).thenReturn(page);

        // Act
        Page<SolicitacaoResponse> resultado = solicitacaoService.listarMinhasSolicitacoes(
                null, null, null, null, null, 0, 10
        );

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("SOL-20231122-0001", resultado.getContent().get(0).protocolo());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(solicitacaoRepository).findByUsuarioIdWithFilters(
                eq(1L), eq(null), eq(null), eq(null), eq(null), eq(null), any(Pageable.class)
        );
    }

    @Test
    @DisplayName("Deve renovar solicitação com sucesso")
    void deveRenovarSolicitacaoComSucesso() {
        // Arrange
        solicitacao.setDataExpiracao(LocalDateTime.now().plusDays(15)); // Dentro do prazo de renovação
        
        SolicitacaoModulo sm1 = SolicitacaoModulo.builder()
                .solicitacao(solicitacao)
                .modulo(modulo1)
                .build();
        solicitacao.getModulos().add(sm1);
        
        Solicitacao novaSolicitacao = Solicitacao.builder()
                .id(2L)
                .protocolo("SOL-20231122-0002")
                .usuario(usuario)
                .justificativa("Renovação da solicitação SOL-20231122-0001")
                .urgente(false)
                .status(StatusSolicitacao.ATIVO)
                .dataSolicitacao(LocalDateTime.now())
                .dataExpiracao(LocalDateTime.now().plusDays(180))
                .solicitacaoOrigem(solicitacao)
                .modulos(new HashSet<>())
                .historico(new HashSet<>())
                .build();

        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(solicitacao));
        when(solicitacaoRepository.countSolicitacoesHoje()).thenReturn(1L);
        when(protocoloGenerator.generate(eq(1L))).thenReturn("SOL-20231122-0002");
        when(moduloDepartamentoRepository.existsByModuloIdAndDepartamento(eq(1L), eq(Departamento.FINANCEIRO))).thenReturn(true);
        when(acessoUsuarioModuloRepository.findModuloIdsAtivosByUsuarioId(eq(1L))).thenReturn(Arrays.asList());
        when(moduloIncompativelRepository.findModulosIncompatibilidadesByModuloId(eq(1L))).thenReturn(Arrays.asList());
        when(acessoUsuarioModuloRepository.countByUsuarioIdAndAtivoTrue(eq(1L))).thenReturn(0L);
        when(acessoUsuarioModuloRepository.findByUsuarioIdAndAtivoTrue(eq(1L))).thenReturn(Arrays.asList());
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(novaSolicitacao);

        // Act
        SolicitacaoResponse resultado = solicitacaoService.renovarSolicitacao(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals("SOL-20231122-0002", resultado.protocolo());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(solicitacaoRepository).findById(eq(1L));
        verify(solicitacaoRepository, atLeast(1)).save(any(Solicitacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao renovar solicitação de outro usuário")
    void deveLancarExcecaoAoRenovarSolicitacaoDeOutroUsuario() {
        // Arrange
        when(authService.getUsuarioLogadoId()).thenReturn(2L); // Usuário diferente
        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(solicitacao));

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> solicitacaoService.renovarSolicitacao(1L));
        assertEquals("Você não tem permissão para renovar esta solicitação", exception.getMessage());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(solicitacaoRepository).findById(eq(1L));
    }

    @Test
    @DisplayName("Deve lançar exceção ao renovar solicitação não ativa")
    void deveLancarExcecaoAoRenovarSolicitacaoNaoAtiva() {
        // Arrange
        solicitacao.setStatus(StatusSolicitacao.CANCELADO);
        
        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(solicitacao));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> solicitacaoService.renovarSolicitacao(1L));
        assertEquals("Apenas solicitações ativas podem ser renovadas", exception.getMessage());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(solicitacaoRepository).findById(eq(1L));
    }

    @Test
    @DisplayName("Deve lançar exceção ao renovar solicitação fora do prazo")
    void deveLancarExcecaoAoRenovarSolicitacaoForaDoPrazo() {
        // Arrange
        solicitacao.setDataExpiracao(LocalDateTime.now().plusDays(60)); // Fora do prazo de 30 dias
        
        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(solicitacao));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> solicitacaoService.renovarSolicitacao(1L));
        assertEquals("Renovação permitida apenas quando faltarem menos de 30 dias para expiração", exception.getMessage());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(solicitacaoRepository).findById(eq(1L));
    }

    @Test
    @DisplayName("Deve negar solicitação quando departamento não tem permissão")
    void deveNegarSolicitacaoQuandoDepartamentoNaoTemPermissao() {
        // Arrange
        List<Modulo> modulos = Arrays.asList(modulo1);
        
        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(usuarioRepository.findById(eq(1L))).thenReturn(Optional.of(usuario));
        when(moduloRepository.findAllByIdsAndAtivoTrue(eq(Arrays.asList(1L)))).thenReturn(modulos);
        when(solicitacaoRepository.existsByUsuarioIdAndModuloIdAndStatus(eq(1L), eq(1L), eq(StatusSolicitacao.ATIVO))).thenReturn(false);
        when(acessoUsuarioModuloRepository.existsByUsuarioIdAndModuloIdAndAtivoTrue(eq(1L), eq(1L))).thenReturn(false);
        when(protocoloGenerator.generate(eq(0L))).thenReturn("SOL-20231122-0003");
        when(solicitacaoRepository.countSolicitacoesHoje()).thenReturn(0L);
        when(moduloDepartamentoRepository.existsByModuloIdAndDepartamento(eq(1L), eq(Departamento.FINANCEIRO))).thenReturn(false);
        
        Solicitacao solicitacaoNegada = Solicitacao.builder()
                .id(1L)
                .protocolo("SOL-20231122-0003")
                .usuario(usuario)
                .justificativa(solicitacaoRequest.justificativa())
                .urgente(false)
                .status(StatusSolicitacao.NEGADO)
                .dataSolicitacao(LocalDateTime.now())
                .motivoNegacao("Departamento sem permissão para acessar este módulo: CRM")
                .modulos(new HashSet<>())
                .historico(new HashSet<>())
                .build();
        
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(solicitacaoNegada);

        SolicitacaoRequest request = new SolicitacaoRequest(
                Arrays.asList(1L),
                "Acesso solicitado para implementação do projeto Phoenix conforme demanda do gestor",
                false
        );

        // Act
        SolicitacaoResponse resultado = solicitacaoService.criarSolicitacao(request);

        // Assert
        assertNotNull(resultado);
        assertEquals("NEGADO", resultado.status());
        assertTrue(resultado.motivoNegacao().contains("Departamento sem permissão"));

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(usuarioRepository).findById(eq(1L));
        verify(moduloRepository).findAllByIdsAndAtivoTrue(eq(Arrays.asList(1L)));
    }

    @Test
    @DisplayName("Deve negar solicitação quando módulos são incompatíveis")
    void deveNegarSolicitacaoQuandoModulosSaoIncompativeis() {
        // Arrange
        List<Modulo> modulos = Arrays.asList(modulo1, modulo2);
        
        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(usuarioRepository.findById(eq(1L))).thenReturn(Optional.of(usuario));
        when(moduloRepository.findAllByIdsAndAtivoTrue(eq(Arrays.asList(1L, 2L)))).thenReturn(modulos);
        when(solicitacaoRepository.existsByUsuarioIdAndModuloIdAndStatus(eq(1L), eq(1L), eq(StatusSolicitacao.ATIVO))).thenReturn(false);
        when(solicitacaoRepository.existsByUsuarioIdAndModuloIdAndStatus(eq(1L), eq(2L), eq(StatusSolicitacao.ATIVO))).thenReturn(false);
        when(acessoUsuarioModuloRepository.existsByUsuarioIdAndModuloIdAndAtivoTrue(eq(1L), eq(1L))).thenReturn(false);
        when(acessoUsuarioModuloRepository.existsByUsuarioIdAndModuloIdAndAtivoTrue(eq(1L), eq(2L))).thenReturn(false);
        when(protocoloGenerator.generate(eq(0L))).thenReturn("SOL-20231122-0004");
        when(solicitacaoRepository.countSolicitacoesHoje()).thenReturn(0L);
        when(moduloDepartamentoRepository.existsByModuloIdAndDepartamento(eq(1L), eq(Departamento.FINANCEIRO))).thenReturn(true);
        when(moduloDepartamentoRepository.existsByModuloIdAndDepartamento(eq(2L), eq(Departamento.FINANCEIRO))).thenReturn(true);
        when(acessoUsuarioModuloRepository.findModuloIdsAtivosByUsuarioId(eq(1L))).thenReturn(Arrays.asList());
        when(moduloIncompativelRepository.findModulosIncompatibilidadesByModuloId(eq(1L))).thenReturn(Arrays.asList(2L));
        
        Solicitacao solicitacaoNegada = Solicitacao.builder()
                .id(1L)
                .protocolo("SOL-20231122-0004")
                .usuario(usuario)
                .justificativa(solicitacaoRequest.justificativa())
                .urgente(false)
                .status(StatusSolicitacao.NEGADO)
                .dataSolicitacao(LocalDateTime.now())
                .motivoNegacao("Não é permitido solicitar módulos incompatíveis entre si")
                .modulos(new HashSet<>())
                .historico(new HashSet<>())
                .build();
        
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(solicitacaoNegada);

        SolicitacaoRequest request = new SolicitacaoRequest(
                Arrays.asList(1L, 2L),
                "Acesso solicitado para implementação do projeto Phoenix conforme demanda do gestor",
                false
        );

        // Act
        SolicitacaoResponse resultado = solicitacaoService.criarSolicitacao(request);

        // Assert
        assertNotNull(resultado);
        assertEquals("NEGADO", resultado.status());
        assertTrue(resultado.motivoNegacao().contains("incompatíveis"));

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(usuarioRepository).findById(eq(1L));
    }

    @Test
    @DisplayName("Deve negar solicitação quando limite de módulos é atingido")
    void deveNegarSolicitacaoQuandoLimiteDeModulosAtingido() {
        // Arrange
        List<Modulo> modulos = Arrays.asList(modulo1);
        
        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(usuarioRepository.findById(eq(1L))).thenReturn(Optional.of(usuario));
        when(moduloRepository.findAllByIdsAndAtivoTrue(eq(Arrays.asList(1L)))).thenReturn(modulos);
        when(solicitacaoRepository.existsByUsuarioIdAndModuloIdAndStatus(eq(1L), eq(1L), eq(StatusSolicitacao.ATIVO))).thenReturn(false);
        when(acessoUsuarioModuloRepository.existsByUsuarioIdAndModuloIdAndAtivoTrue(eq(1L), eq(1L))).thenReturn(false);
        when(protocoloGenerator.generate(eq(0L))).thenReturn("SOL-20231122-0005");
        when(solicitacaoRepository.countSolicitacoesHoje()).thenReturn(0L);
        when(moduloDepartamentoRepository.existsByModuloIdAndDepartamento(eq(1L), eq(Departamento.FINANCEIRO))).thenReturn(true);
        when(acessoUsuarioModuloRepository.findModuloIdsAtivosByUsuarioId(eq(1L))).thenReturn(Arrays.asList());
        when(moduloIncompativelRepository.findModulosIncompatibilidadesByModuloId(eq(1L))).thenReturn(Arrays.asList());
        when(acessoUsuarioModuloRepository.countByUsuarioIdAndAtivoTrue(eq(1L))).thenReturn(5L); // Já tem 5 módulos (limite para FINANCEIRO)
        
        Solicitacao solicitacaoNegada = Solicitacao.builder()
                .id(1L)
                .protocolo("SOL-20231122-0005")
                .usuario(usuario)
                .justificativa(solicitacaoRequest.justificativa())
                .urgente(false)
                .status(StatusSolicitacao.NEGADO)
                .dataSolicitacao(LocalDateTime.now())
                .motivoNegacao("Limite de módulos ativos atingido")
                .modulos(new HashSet<>())
                .historico(new HashSet<>())
                .build();
        
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(solicitacaoNegada);

        SolicitacaoRequest request = new SolicitacaoRequest(
                Arrays.asList(1L),
                "Acesso solicitado para implementação do projeto Phoenix conforme demanda do gestor",
                false
        );

        // Act
        SolicitacaoResponse resultado = solicitacaoService.criarSolicitacao(request);

        // Assert
        assertNotNull(resultado);
        assertEquals("NEGADO", resultado.status());
        assertEquals("Limite de módulos ativos atingido", resultado.motivoNegacao());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(acessoUsuarioModuloRepository).countByUsuarioIdAndAtivoTrue(eq(1L));
    }

    @Test
    @DisplayName("Deve permitir mais módulos para departamento TI")
    void devePermitirMaisModulosParaDepartamentoTI() {
        // Arrange
        Usuario usuarioTI = Usuario.builder()
                .id(2L)
                .nome("Admin TI")
                .email("admin@empresa.com")
                .senha("senha")
                .departamento(Departamento.TI)
                .ativo(true)
                .build();
        
        List<Modulo> modulos = Arrays.asList(modulo1);
        
        when(authService.getUsuarioLogadoId()).thenReturn(2L);
        when(usuarioRepository.findById(eq(2L))).thenReturn(Optional.of(usuarioTI));
        when(moduloRepository.findAllByIdsAndAtivoTrue(eq(Arrays.asList(1L)))).thenReturn(modulos);
        when(solicitacaoRepository.existsByUsuarioIdAndModuloIdAndStatus(eq(2L), eq(1L), eq(StatusSolicitacao.ATIVO))).thenReturn(false);
        when(acessoUsuarioModuloRepository.existsByUsuarioIdAndModuloIdAndAtivoTrue(eq(2L), eq(1L))).thenReturn(false);
        when(protocoloGenerator.generate(eq(0L))).thenReturn("SOL-20231122-0006");
        when(solicitacaoRepository.countSolicitacoesHoje()).thenReturn(0L);
        // TI tem acesso a todos os módulos
        when(acessoUsuarioModuloRepository.findModuloIdsAtivosByUsuarioId(eq(2L))).thenReturn(Arrays.asList());
        when(moduloIncompativelRepository.findModulosIncompatibilidadesByModuloId(eq(1L))).thenReturn(Arrays.asList());
        when(acessoUsuarioModuloRepository.countByUsuarioIdAndAtivoTrue(eq(2L))).thenReturn(5L); // TI pode ter até 10
        
        Solicitacao solicitacaoAprovada = Solicitacao.builder()
                .id(2L)
                .protocolo("SOL-20231122-0006")
                .usuario(usuarioTI)
                .justificativa("Acesso solicitado para implementação do projeto Phoenix conforme demanda do gestor")
                .urgente(false)
                .status(StatusSolicitacao.ATIVO)
                .dataSolicitacao(LocalDateTime.now())
                .dataExpiracao(LocalDateTime.now().plusDays(180))
                .modulos(new HashSet<>())
                .historico(new HashSet<>())
                .build();
        
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(solicitacaoAprovada);

        SolicitacaoRequest request = new SolicitacaoRequest(
                Arrays.asList(1L),
                "Acesso solicitado para implementação do projeto Phoenix conforme demanda do gestor",
                false
        );

        // Act
        SolicitacaoResponse resultado = solicitacaoService.criarSolicitacao(request);

        // Assert
        assertNotNull(resultado);
        assertEquals("ATIVO", resultado.status());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(usuarioRepository).findById(eq(2L));
    }

    @Test
    @DisplayName("Deve negar renovação de solicitação quando regras de negócio não são atendidas")
    void deveNegarRenovacaoQuandoRegrasNaoSaoAtendidas() {
        // Arrange
        solicitacao.setDataExpiracao(LocalDateTime.now().plusDays(15));
        
        SolicitacaoModulo sm1 = SolicitacaoModulo.builder()
                .solicitacao(solicitacao)
                .modulo(modulo1)
                .build();
        solicitacao.getModulos().add(sm1);
        
        Solicitacao renovacaoNegada = Solicitacao.builder()
                .id(2L)
                .protocolo("SOL-20231122-0007")
                .usuario(usuario)
                .justificativa("Renovação da solicitação SOL-20231122-0001")
                .urgente(false)
                .status(StatusSolicitacao.NEGADO)
                .dataSolicitacao(LocalDateTime.now())
                .motivoNegacao("Limite de módulos ativos atingido")
                .solicitacaoOrigem(solicitacao)
                .modulos(new HashSet<>())
                .historico(new HashSet<>())
                .build();

        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(solicitacao));
        when(solicitacaoRepository.countSolicitacoesHoje()).thenReturn(1L);
        when(protocoloGenerator.generate(eq(1L))).thenReturn("SOL-20231122-0007");
        when(moduloDepartamentoRepository.existsByModuloIdAndDepartamento(eq(1L), eq(Departamento.FINANCEIRO))).thenReturn(true);
        when(acessoUsuarioModuloRepository.findModuloIdsAtivosByUsuarioId(eq(1L))).thenReturn(Arrays.asList());
        when(moduloIncompativelRepository.findModulosIncompatibilidadesByModuloId(eq(1L))).thenReturn(Arrays.asList());
        when(acessoUsuarioModuloRepository.countByUsuarioIdAndAtivoTrue(eq(1L))).thenReturn(5L); // Limite atingido
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(renovacaoNegada);

        // Act
        SolicitacaoResponse resultado = solicitacaoService.renovarSolicitacao(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals("NEGADO", resultado.status());
        assertEquals("Limite de módulos ativos atingido", resultado.motivoNegacao());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(solicitacaoRepository).findById(eq(1L));
    }

    @Test
    @DisplayName("Deve negar solicitação quando módulo é incompatível com módulos já ativos")
    void deveNegarQuandoModuloIncompativelComAtivoExistente() {
        // Arrange
        List<Modulo> modulos = Arrays.asList(modulo2);
        
        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(usuarioRepository.findById(eq(1L))).thenReturn(Optional.of(usuario));
        when(moduloRepository.findAllByIdsAndAtivoTrue(eq(Arrays.asList(2L)))).thenReturn(modulos);
        when(solicitacaoRepository.existsByUsuarioIdAndModuloIdAndStatus(eq(1L), eq(2L), eq(StatusSolicitacao.ATIVO))).thenReturn(false);
        when(acessoUsuarioModuloRepository.existsByUsuarioIdAndModuloIdAndAtivoTrue(eq(1L), eq(2L))).thenReturn(false);
        when(protocoloGenerator.generate(eq(0L))).thenReturn("SOL-20231122-0008");
        when(solicitacaoRepository.countSolicitacoesHoje()).thenReturn(0L);
        when(moduloDepartamentoRepository.existsByModuloIdAndDepartamento(eq(2L), eq(Departamento.FINANCEIRO))).thenReturn(true);
        when(acessoUsuarioModuloRepository.findModuloIdsAtivosByUsuarioId(eq(1L))).thenReturn(Arrays.asList(1L)); // Já tem módulo 1 ativo
        when(moduloIncompativelRepository.findModulosIncompatibilidadesByModuloId(eq(2L))).thenReturn(Arrays.asList(1L)); // Módulo 2 incompatível com 1
        
        Solicitacao solicitacaoNegada = Solicitacao.builder()
                .id(1L)
                .protocolo("SOL-20231122-0008")
                .usuario(usuario)
                .justificativa("Acesso solicitado para implementação do projeto Phoenix conforme demanda do gestor")
                .urgente(false)
                .status(StatusSolicitacao.NEGADO)
                .dataSolicitacao(LocalDateTime.now())
                .motivoNegacao("Módulo incompatível com outro módulo já ativo em seu perfil")
                .modulos(new HashSet<>())
                .historico(new HashSet<>())
                .build();
        
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(solicitacaoNegada);

        SolicitacaoRequest request = new SolicitacaoRequest(
                Arrays.asList(2L),
                "Acesso solicitado para implementação do projeto Phoenix conforme demanda do gestor",
                false
        );

        // Act
        SolicitacaoResponse resultado = solicitacaoService.criarSolicitacao(request);

        // Assert
        assertNotNull(resultado);
        assertEquals("NEGADO", resultado.status());
        assertTrue(resultado.motivoNegacao().contains("incompatível"));

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(acessoUsuarioModuloRepository).findModuloIdsAtivosByUsuarioId(eq(1L));
        verify(moduloIncompativelRepository).findModulosIncompatibilidadesByModuloId(eq(2L));
    }

    @Test
    @DisplayName("Deve listar solicitações com filtros de status")
    void deveListarSolicitacoesComFiltrosDeStatus() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(Arrays.asList(solicitacao), pageable, 1);

        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(solicitacaoRepository.findByUsuarioIdWithFilters(
                eq(1L), eq("ATIVO"), eq(null), eq(null), eq(null), eq(null), any(Pageable.class)
        )).thenReturn(page);

        // Act
        Page<SolicitacaoResponse> resultado = solicitacaoService.listarMinhasSolicitacoes(
                StatusSolicitacao.ATIVO, null, null, null, null, 0, 10
        );

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(solicitacaoRepository).findByUsuarioIdWithFilters(
                eq(1L), eq("ATIVO"), eq(null), eq(null), eq(null), eq(null), any(Pageable.class)
        );
    }

    @Test
    @DisplayName("Deve listar solicitações com filtro urgente")
    void deveListarSolicitacoesComFiltroUrgente() {
        // Arrange
        solicitacao.setUrgente(true);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Solicitacao> page = new PageImpl<>(Arrays.asList(solicitacao), pageable, 1);

        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(solicitacaoRepository.findByUsuarioIdWithFilters(
                eq(1L), eq(null), eq(true), eq(null), eq(null), eq(null), any(Pageable.class)
        )).thenReturn(page);

        // Act
        Page<SolicitacaoResponse> resultado = solicitacaoService.listarMinhasSolicitacoes(
                null, true, null, null, null, 0, 10
        );

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertTrue(resultado.getContent().get(0).urgente());

        // Verify
        verify(authService).getUsuarioLogadoId();
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar solicitação de outro usuário")
    void deveLancarExcecaoAoBuscarSolicitacaoDeOutroUsuario() {
        // Arrange
        when(authService.getUsuarioLogadoId()).thenReturn(999L); // Outro usuário
        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(solicitacao));

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> solicitacaoService.buscarSolicitacaoPorId(1L));
        assertEquals("Você não tem permissão para visualizar esta solicitação", exception.getMessage());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(solicitacaoRepository).findById(eq(1L));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar solicitação de outro usuário")
    void deveLancarExcecaoAoCancelarSolicitacaoDeOutroUsuario() {
        // Arrange
        CancelarSolicitacaoRequest request = new CancelarSolicitacaoRequest("Não preciso mais");
        
        when(authService.getUsuarioLogadoId()).thenReturn(999L); // Outro usuário
        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(solicitacao));

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> solicitacaoService.cancelarSolicitacao(1L, request));
        assertEquals("Você não tem permissão para cancelar esta solicitação", exception.getMessage());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(solicitacaoRepository).findById(eq(1L));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar solicitação não ativa")
    void deveLancarExcecaoAoCancelarSolicitacaoNaoAtiva() {
        // Arrange
        solicitacao.setStatus(StatusSolicitacao.CANCELADO);
        CancelarSolicitacaoRequest request = new CancelarSolicitacaoRequest("Não preciso mais");
        
        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(solicitacao));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> solicitacaoService.cancelarSolicitacao(1L, request));
        assertEquals("Apenas solicitações ativas podem ser canceladas", exception.getMessage());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(solicitacaoRepository).findById(eq(1L));
    }

    @Test
    @DisplayName("Deve lançar exceção quando já existe acesso ativo")
    void deveLancarExcecaoQuandoJaExisteAcessoAtivo() {
        // Arrange
        List<Modulo> modulos = Arrays.asList(modulo1);
        
        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(usuarioRepository.findById(eq(1L))).thenReturn(Optional.of(usuario));
        when(moduloRepository.findAllByIdsAndAtivoTrue(eq(Arrays.asList(1L)))).thenReturn(modulos);
        when(solicitacaoRepository.existsByUsuarioIdAndModuloIdAndStatus(eq(1L), eq(1L), eq(StatusSolicitacao.ATIVO))).thenReturn(false);
        when(acessoUsuarioModuloRepository.existsByUsuarioIdAndModuloIdAndAtivoTrue(eq(1L), eq(1L))).thenReturn(true);

        SolicitacaoRequest request = new SolicitacaoRequest(
                Arrays.asList(1L),
                "Acesso solicitado para implementação do projeto Phoenix conforme demanda do gestor",
                false
        );

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> solicitacaoService.criarSolicitacao(request));
        assertEquals("Você já possui acesso ativo para um dos módulos selecionados", exception.getMessage());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(acessoUsuarioModuloRepository).existsByUsuarioIdAndModuloIdAndAtivoTrue(eq(1L), eq(1L));
    }

    @Test
    @DisplayName("Deve criar solicitação com urgência")
    void deveCriarSolicitacaoComUrgencia() {
        // Arrange
        List<Modulo> modulos = Arrays.asList(modulo1, modulo2);
        
        when(authService.getUsuarioLogadoId()).thenReturn(1L);
        when(usuarioRepository.findById(eq(1L))).thenReturn(Optional.of(usuario));
        when(moduloRepository.findAllByIdsAndAtivoTrue(eq(Arrays.asList(1L, 2L)))).thenReturn(modulos);
        when(solicitacaoRepository.existsByUsuarioIdAndModuloIdAndStatus(eq(1L), eq(1L), eq(StatusSolicitacao.ATIVO))).thenReturn(false);
        when(solicitacaoRepository.existsByUsuarioIdAndModuloIdAndStatus(eq(1L), eq(2L), eq(StatusSolicitacao.ATIVO))).thenReturn(false);
        when(acessoUsuarioModuloRepository.existsByUsuarioIdAndModuloIdAndAtivoTrue(eq(1L), eq(1L))).thenReturn(false);
        when(acessoUsuarioModuloRepository.existsByUsuarioIdAndModuloIdAndAtivoTrue(eq(1L), eq(2L))).thenReturn(false);
        when(protocoloGenerator.generate(eq(0L))).thenReturn("SOL-20231122-0009");
        when(solicitacaoRepository.countSolicitacoesHoje()).thenReturn(0L);
        when(moduloDepartamentoRepository.existsByModuloIdAndDepartamento(eq(1L), eq(Departamento.FINANCEIRO))).thenReturn(true);
        when(moduloDepartamentoRepository.existsByModuloIdAndDepartamento(eq(2L), eq(Departamento.FINANCEIRO))).thenReturn(true);
        when(acessoUsuarioModuloRepository.findModuloIdsAtivosByUsuarioId(eq(1L))).thenReturn(Arrays.asList());
        when(moduloIncompativelRepository.findModulosIncompatibilidadesByModuloId(eq(1L))).thenReturn(Arrays.asList());
        when(moduloIncompativelRepository.findModulosIncompatibilidadesByModuloId(eq(2L))).thenReturn(Arrays.asList());
        when(acessoUsuarioModuloRepository.countByUsuarioIdAndAtivoTrue(eq(1L))).thenReturn(0L);
        
        Solicitacao solicitacaoUrgente = Solicitacao.builder()
                .id(1L)
                .protocolo("SOL-20231122-0009")
                .usuario(usuario)
                .justificativa("Acesso solicitado para implementação do projeto Phoenix conforme demanda do gestor")
                .urgente(true)
                .status(StatusSolicitacao.ATIVO)
                .dataSolicitacao(LocalDateTime.now())
                .dataExpiracao(LocalDateTime.now().plusDays(180))
                .modulos(new HashSet<>())
                .historico(new HashSet<>())
                .build();
        
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenReturn(solicitacaoUrgente);

        SolicitacaoRequest request = new SolicitacaoRequest(
                Arrays.asList(1L, 2L),
                "Acesso solicitado para implementação do projeto Phoenix conforme demanda do gestor",
                true
        );

        // Act
        SolicitacaoResponse resultado = solicitacaoService.criarSolicitacao(request);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.urgente());
        assertEquals("ATIVO", resultado.status());

        // Verify
        verify(authService).getUsuarioLogadoId();
        verify(usuarioRepository).findById(eq(1L));
    }
}


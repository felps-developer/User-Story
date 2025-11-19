package com.supera.accessrequest.controller;

import com.supera.accessrequest.dto.CancelarSolicitacaoRequest;
import com.supera.accessrequest.dto.SolicitacaoRequest;
import com.supera.accessrequest.dto.SolicitacaoResponse;
import com.supera.accessrequest.enums.StatusSolicitacao;
import com.supera.accessrequest.service.SolicitacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/solicitacoes")
@RequiredArgsConstructor
@Tag(name = "Solicitações", description = "Endpoints para gerenciamento de solicitações de acesso")
@SecurityRequirement(name = "Bearer Authentication")
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    @PostMapping
    @Operation(summary = "Criar nova solicitação", 
               description = "Cria uma nova solicitação de acesso a módulos com validação automática das regras de negócio")
    public ResponseEntity<ApiResponse> criarSolicitacao(@Valid @RequestBody SolicitacaoRequest request) {
        SolicitacaoResponse solicitacao = solicitacaoService.criarSolicitacao(request);
        
        String mensagem;
        if ("ATIVO".equals(solicitacao.status())) {
            mensagem = String.format("Solicitação criada com sucesso! Protocolo: %s. Seus acessos já estão disponíveis!", 
                    solicitacao.protocolo());
        } else {
            mensagem = String.format("Solicitação negada. Motivo: %s", solicitacao.motivoNegacao());
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(mensagem, solicitacao));
    }

    @GetMapping
    @Operation(summary = "Listar minhas solicitações", 
               description = "Lista todas as solicitações do usuário autenticado com filtros e paginação")
    public ResponseEntity<Page<SolicitacaoResponse>> listarMinhasSolicitacoes(
            @Parameter(description = "Status da solicitação") 
            @RequestParam(required = false) StatusSolicitacao status,
            
            @Parameter(description = "Filtrar por urgente")
            @RequestParam(required = false) Boolean urgente,
            
            @Parameter(description = "Data início (formato: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            
            @Parameter(description = "Data fim (formato: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            
            @Parameter(description = "Pesquisa por protocolo ou nome do módulo")
            @RequestParam(required = false) String pesquisa,
            
            @Parameter(description = "Número da página (começa em 0)")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<SolicitacaoResponse> solicitacoes = solicitacaoService.listarMinhasSolicitacoes(
                status, urgente, dataInicio, dataFim, pesquisa, page, size
        );
        return ResponseEntity.ok(solicitacoes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar solicitação por ID", 
               description = "Retorna os detalhes completos de uma solicitação específica")
    public ResponseEntity<SolicitacaoResponse> buscarSolicitacaoPorId(
            @Parameter(description = "ID da solicitação") 
            @PathVariable Long id
    ) {
        SolicitacaoResponse solicitacao = solicitacaoService.buscarSolicitacaoPorId(id);
        return ResponseEntity.ok(solicitacao);
    }

    @PutMapping("/{id}/renovar")
    @Operation(summary = "Renovar solicitação", 
               description = "Renova uma solicitação ativa quando faltarem menos de 30 dias para expiração")
    public ResponseEntity<ApiResponse> renovarSolicitacao(
            @Parameter(description = "ID da solicitação") 
            @PathVariable Long id
    ) {
        SolicitacaoResponse solicitacao = solicitacaoService.renovarSolicitacao(id);
        
        String mensagem;
        if ("ATIVO".equals(solicitacao.status())) {
            mensagem = String.format("Solicitação renovada com sucesso! Novo protocolo: %s", 
                    solicitacao.protocolo());
        } else {
            mensagem = String.format("Renovação negada. Motivo: %s", solicitacao.motivoNegacao());
        }

        return ResponseEntity.ok(new ApiResponse(mensagem, solicitacao));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar solicitação", 
               description = "Cancela uma solicitação ativa e revoga os acessos concedidos")
    public ResponseEntity<ApiResponse> cancelarSolicitacao(
            @Parameter(description = "ID da solicitação") 
            @PathVariable Long id,
            
            @Valid @RequestBody CancelarSolicitacaoRequest request
    ) {
        solicitacaoService.cancelarSolicitacao(id, request);
        return ResponseEntity.ok(new ApiResponse("Solicitação cancelada com sucesso", null));
    }

    // Response record
    public record ApiResponse(String mensagem, Object dados) {}
}


package com.supera.accessrequest.controller;

import com.supera.accessrequest.dto.CancelarSolicitacaoRequest;
import com.supera.accessrequest.dto.SolicitacaoRequest;
import com.supera.accessrequest.dto.SolicitacaoResponse;
import com.supera.accessrequest.enums.StatusSolicitacao;
import com.supera.accessrequest.service.SolicitacaoService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SolicitacaoController - Testes Unitários")
class SolicitacaoControllerTest {

    @Mock
    private SolicitacaoService solicitacaoService;

    @InjectMocks
    private SolicitacaoController solicitacaoController;

    private SolicitacaoRequest solicitacaoRequest;
    private SolicitacaoResponse solicitacaoResponse;

    @BeforeEach
    void setUp() {
        solicitacaoRequest = new SolicitacaoRequest(
                Arrays.asList(1L, 2L),
                "Preciso acessar os módulos para realizar minhas atividades diárias",
                false
        );

        solicitacaoResponse = new SolicitacaoResponse(
                1L,
                "SOL-20231122-0001",
                Arrays.asList(
                        new SolicitacaoResponse.ModuloSimpleResponse(1L, "CRM"),
                        new SolicitacaoResponse.ModuloSimpleResponse(2L, "Financeiro")
                ),
                "ATIVO",
                "Preciso acessar os módulos para realizar minhas atividades diárias",
                false,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(180),
                1L,
                "João Silva",
                "TI",
                Arrays.asList()
        );
    }

    @Test
    @DisplayName("Deve criar solicitação com sucesso e retornar 201 CREATED com mensagem de aprovação")
    void deveCriarSolicitacaoComSucesso() {
        // Arrange
        when(solicitacaoService.criarSolicitacao(eq(solicitacaoRequest))).thenReturn(solicitacaoResponse);

        // Act
        ResponseEntity<SolicitacaoController.ApiResponse> response = 
                solicitacaoController.criarSolicitacao(solicitacaoRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().mensagem().contains("Solicitação criada com sucesso"));
        assertTrue(response.getBody().mensagem().contains("SOL-20231122-0001"));
        assertNotNull(response.getBody().dados());

        // Verify
        verify(solicitacaoService).criarSolicitacao(eq(solicitacaoRequest));
    }

    @Test
    @DisplayName("Deve criar solicitação negada e retornar mensagem de negação")
    void deveCriarSolicitacaoNegada() {
        // Arrange
        SolicitacaoResponse negadaResponse = new SolicitacaoResponse(
                1L,
                "SOL-20231122-0001",
                Arrays.asList(new SolicitacaoResponse.ModuloSimpleResponse(1L, "CRM")),
                "NEGADO",
                "Justificativa",
                false,
                "Departamento sem permissão",
                null,
                LocalDateTime.now(),
                null,
                1L,
                "João Silva",
                "TI",
                Arrays.asList()
        );
        when(solicitacaoService.criarSolicitacao(eq(solicitacaoRequest))).thenReturn(negadaResponse);

        // Act
        ResponseEntity<SolicitacaoController.ApiResponse> response = 
                solicitacaoController.criarSolicitacao(solicitacaoRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().mensagem().contains("Solicitação negada"));
        assertTrue(response.getBody().mensagem().contains("Departamento sem permissão"));

        // Verify
        verify(solicitacaoService).criarSolicitacao(eq(solicitacaoRequest));
    }

    @Test
    @DisplayName("Deve listar minhas solicitações com sucesso")
    void deveListarMinhasSolicitacoesComSucesso() {
        // Arrange
        Page<SolicitacaoResponse> page = new PageImpl<>(Arrays.asList(solicitacaoResponse), PageRequest.of(0, 10), 1);
        when(solicitacaoService.listarMinhasSolicitacoes(
                eq(StatusSolicitacao.ATIVO),
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                eq(0),
                eq(10)
        )).thenReturn(page);

        // Act
        ResponseEntity<Page<SolicitacaoResponse>> response = solicitacaoController.listarMinhasSolicitacoes(
                StatusSolicitacao.ATIVO, null, null, null, null, 0, 10
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());

        // Verify
        verify(solicitacaoService).listarMinhasSolicitacoes(
                eq(StatusSolicitacao.ATIVO),
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                eq(0),
                eq(10)
        );
    }

    @Test
    @DisplayName("Deve buscar solicitação por ID com sucesso")
    void deveBuscarSolicitacaoPorIdComSucesso() {
        // Arrange
        Long id = 1L;
        when(solicitacaoService.buscarSolicitacaoPorId(eq(id))).thenReturn(solicitacaoResponse);

        // Act
        ResponseEntity<SolicitacaoResponse> response = solicitacaoController.buscarSolicitacaoPorId(id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().id());

        // Verify
        verify(solicitacaoService).buscarSolicitacaoPorId(eq(id));
    }

    @Test
    @DisplayName("Deve renovar solicitação com sucesso")
    void deveRenovarSolicitacaoComSucesso() {
        // Arrange
        Long id = 1L;
        SolicitacaoResponse renovadaResponse = new SolicitacaoResponse(
                2L,
                "SOL-20231122-0002",
                Arrays.asList(new SolicitacaoResponse.ModuloSimpleResponse(1L, "CRM")),
                "ATIVO",
                "Renovação",
                false,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(180),
                1L,
                "João Silva",
                "TI",
                Arrays.asList()
        );
        when(solicitacaoService.renovarSolicitacao(eq(id))).thenReturn(renovadaResponse);

        // Act
        ResponseEntity<SolicitacaoController.ApiResponse> response = solicitacaoController.renovarSolicitacao(id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().mensagem().contains("Solicitação renovada com sucesso"));
        assertTrue(response.getBody().mensagem().contains("SOL-20231122-0002"));

        // Verify
        verify(solicitacaoService).renovarSolicitacao(eq(id));
    }

    @Test
    @DisplayName("Deve renovar solicitação negada e retornar mensagem de negação")
    void deveRenovarSolicitacaoNegada() {
        // Arrange
        Long id = 1L;
        SolicitacaoResponse negadaResponse = new SolicitacaoResponse(
                2L,
                "SOL-20231122-0002",
                Arrays.asList(new SolicitacaoResponse.ModuloSimpleResponse(1L, "CRM")),
                "NEGADO",
                "Renovação",
                false,
                "Limite de módulos atingido",
                null,
                LocalDateTime.now(),
                null,
                1L,
                "João Silva",
                "TI",
                Arrays.asList()
        );
        when(solicitacaoService.renovarSolicitacao(eq(id))).thenReturn(negadaResponse);

        // Act
        ResponseEntity<SolicitacaoController.ApiResponse> response = solicitacaoController.renovarSolicitacao(id);

        // Assert
        assertNotNull(response);
        assertTrue(response.getBody().mensagem().contains("Renovação negada"));
        assertTrue(response.getBody().mensagem().contains("Limite de módulos atingido"));

        // Verify
        verify(solicitacaoService).renovarSolicitacao(eq(id));
    }

    @Test
    @DisplayName("Deve cancelar solicitação com sucesso")
    void deveCancelarSolicitacaoComSucesso() {
        // Arrange
        Long id = 1L;
        CancelarSolicitacaoRequest cancelarRequest = new CancelarSolicitacaoRequest(
                "Não preciso mais deste acesso"
        );
        doNothing().when(solicitacaoService).cancelarSolicitacao(eq(id), eq(cancelarRequest));

        // Act
        ResponseEntity<SolicitacaoController.ApiResponse> response = 
                solicitacaoController.cancelarSolicitacao(id, cancelarRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().mensagem().contains("Solicitação cancelada com sucesso"));
        assertNull(response.getBody().dados());

        // Verify
        verify(solicitacaoService).cancelarSolicitacao(eq(id), eq(cancelarRequest));
    }

    @Test
    @DisplayName("Deve chamar service com parâmetros corretos ao listar solicitações")
    void deveChamarServiceComParametrosCorretosAoListarSolicitacoes() {
        // Arrange
        StatusSolicitacao status = StatusSolicitacao.ATIVO;
        Boolean urgente = true;
        LocalDateTime dataInicio = LocalDateTime.now().minusDays(7);
        LocalDateTime dataFim = LocalDateTime.now();
        String pesquisa = "SOL-2023";
        int page = 0;
        int size = 20;

        Page<SolicitacaoResponse> pageResponse = new PageImpl<>(Arrays.asList(), PageRequest.of(page, size), 0);
        when(solicitacaoService.listarMinhasSolicitacoes(
                eq(status),
                eq(urgente),
                eq(dataInicio),
                eq(dataFim),
                eq(pesquisa),
                eq(page),
                eq(size)
        )).thenReturn(pageResponse);

        // Act
        solicitacaoController.listarMinhasSolicitacoes(status, urgente, dataInicio, dataFim, pesquisa, page, size);

        // Verify
        verify(solicitacaoService).listarMinhasSolicitacoes(
                eq(status),
                eq(urgente),
                eq(dataInicio),
                eq(dataFim),
                eq(pesquisa),
                eq(page),
                eq(size)
        );
    }
}


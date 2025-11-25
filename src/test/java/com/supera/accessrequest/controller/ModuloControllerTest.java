package com.supera.accessrequest.controller;

import com.supera.accessrequest.dto.ModuloResponse;
import com.supera.accessrequest.service.ModuloService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ModuloController - Testes Unitários")
class ModuloControllerTest {

    @Mock
    private ModuloService moduloService;

    @InjectMocks
    private ModuloController moduloController;

    private List<ModuloResponse> modulos;

    @BeforeEach
    void setUp() {
        modulos = Arrays.asList(
                new ModuloResponse(
                        1L,
                        "CRM",
                        "Sistema de gestão de clientes",
                        true,
                        Arrays.asList("TI", "FINANCEIRO"),
                        Arrays.asList()
                ),
                new ModuloResponse(
                        2L,
                        "Financeiro",
                        "Sistema financeiro",
                        true,
                        Arrays.asList("TI", "FINANCEIRO"),
                        Arrays.asList()
                )
        );
    }

    @Test
    @DisplayName("Deve listar módulos disponíveis com sucesso e retornar 200 OK")
    void deveListarModulosDisponiveisComSucesso() {
        // Arrange
        when(moduloService.listarModulosDisponiveis()).thenReturn(modulos);

        // Act
        ResponseEntity<List<ModuloResponse>> response = moduloController.listarModulosDisponiveis();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("CRM", response.getBody().get(0).nome());
        assertEquals("Financeiro", response.getBody().get(1).nome());

        // Verify
        verify(moduloService).listarModulosDisponiveis();
        verifyNoMoreInteractions(moduloService);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver módulos")
    void deveRetornarListaVaziaQuandoNaoHouverModulos() {
        // Arrange
        when(moduloService.listarModulosDisponiveis()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<List<ModuloResponse>> response = moduloController.listarModulosDisponiveis();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        // Verify
        verify(moduloService).listarModulosDisponiveis();
    }

    @Test
    @DisplayName("Deve chamar ModuloService.listarModulosDisponiveis corretamente")
    void deveChamarModuloServiceListarModulosDisponiveis() {
        // Arrange
        when(moduloService.listarModulosDisponiveis()).thenReturn(modulos);

        // Act
        moduloController.listarModulosDisponiveis();

        // Verify
        verify(moduloService, times(1)).listarModulosDisponiveis();
    }
}


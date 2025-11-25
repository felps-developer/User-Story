package com.supera.accessrequest.controller;

import com.supera.accessrequest.dto.LoginRequest;
import com.supera.accessrequest.dto.LoginResponse;
import com.supera.accessrequest.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController - Testes Unitários")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("joao.silva@empresa.com", "senha123");
        loginResponse = new LoginResponse(
                "jwt.token.here",
                "Bearer",
                1L,
                "João Silva",
                "joao.silva@empresa.com",
                "TI"
        );
    }

    @Test
    @DisplayName("Deve realizar login com sucesso e retornar 200 OK")
    void deveRealizarLoginComSucesso() {
        // Arrange
        when(authService.login(eq(loginRequest))).thenReturn(loginResponse);

        // Act
        ResponseEntity<LoginResponse> response = authController.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(loginResponse.token(), response.getBody().token());
        assertEquals(loginResponse.tipo(), response.getBody().tipo());
        assertEquals(loginResponse.usuarioId(), response.getBody().usuarioId());
        assertEquals(loginResponse.nome(), response.getBody().nome());
        assertEquals(loginResponse.email(), response.getBody().email());
        assertEquals(loginResponse.departamento(), response.getBody().departamento());

        // Verify
        verify(authService).login(eq(loginRequest));
        verifyNoMoreInteractions(authService);
    }

    @Test
    @DisplayName("Deve chamar AuthService.login com o LoginRequest correto")
    void deveChamarAuthServiceLoginComLoginRequestCorreto() {
        // Arrange
        when(authService.login(eq(loginRequest))).thenReturn(loginResponse);

        // Act
        authController.login(loginRequest);

        // Verify
        verify(authService, times(1)).login(eq(loginRequest));
    }

    @Test
    @DisplayName("Deve retornar LoginResponse do AuthService")
    void deveRetornarLoginResponseDoAuthService() {
        // Arrange
        LoginResponse customResponse = new LoginResponse(
                "custom.token",
                "Bearer",
                2L,
                "Maria Santos",
                "maria@empresa.com",
                "FINANCEIRO"
        );
        when(authService.login(eq(loginRequest))).thenReturn(customResponse);

        // Act
        ResponseEntity<LoginResponse> response = authController.login(loginRequest);

        // Assert
        assertEquals(customResponse.token(), response.getBody().token());
        assertEquals(customResponse.email(), response.getBody().email());
    }
}


package com.supera.accessrequest.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("GlobalExceptionHandler - Testes Unitários")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Deve tratar BusinessException com status BAD_REQUEST")
    void deveTratarBusinessException() {
        // Arrange
        String mensagem = "Erro de negócio";
        BusinessException exception = new BusinessException(mensagem);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                exceptionHandler.handleBusinessException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().status());
        assertEquals(mensagem, response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("Deve tratar ResourceNotFoundException com status NOT_FOUND")
    void deveTratarResourceNotFoundException() {
        // Arrange
        String mensagem = "Recurso não encontrado";
        ResourceNotFoundException exception = new ResourceNotFoundException(mensagem);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                exceptionHandler.handleResourceNotFoundException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().status());
        assertEquals(mensagem, response.getBody().message());
    }

    @Test
    @DisplayName("Deve tratar UnauthorizedException com status UNAUTHORIZED")
    void deveTratarUnauthorizedException() {
        // Arrange
        String mensagem = "Acesso não autorizado";
        UnauthorizedException exception = new UnauthorizedException(mensagem);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                exceptionHandler.handleUnauthorizedException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getBody().status());
        assertEquals(mensagem, response.getBody().message());
    }

    @Test
    @DisplayName("Deve tratar AuthenticationException com status UNAUTHORIZED")
    void deveTratarAuthenticationException() {
        // Arrange
        AuthenticationException exception = mock(AuthenticationException.class);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                exceptionHandler.handleAuthenticationException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getBody().status());
        assertEquals("Credenciais inválidas", response.getBody().message());
    }

    @Test
    @DisplayName("Deve tratar BadCredentialsException com status UNAUTHORIZED")
    void deveTratarBadCredentialsException() {
        // Arrange
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                exceptionHandler.handleAuthenticationException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Credenciais inválidas", response.getBody().message());
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException com status BAD_REQUEST")
    void deveTratarMethodArgumentNotValidException() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        org.springframework.validation.BindingResult bindingResult = mock(org.springframework.validation.BindingResult.class);
        FieldError fieldError1 = new FieldError("solicitacaoRequest", "justificativa", "Justificativa é obrigatória");
        FieldError fieldError2 = new FieldError("solicitacaoRequest", "modulosIds", "Módulos são obrigatórios");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(java.util.Arrays.asList(fieldError1, fieldError2));

        // Act
        ResponseEntity<GlobalExceptionHandler.ValidationErrorResponse> response = 
                exceptionHandler.handleValidationExceptions(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().status());
        assertEquals("Erro de validação", response.getBody().message());
        assertNotNull(response.getBody().errors());
        assertEquals(2, response.getBody().errors().size());
        assertEquals("Justificativa é obrigatória", response.getBody().errors().get("justificativa"));
        assertEquals("Módulos são obrigatórios", response.getBody().errors().get("modulosIds"));
    }

    @Test
    @DisplayName("Deve tratar Exception genérica com status INTERNAL_SERVER_ERROR")
    void deveTratarExceptionGenerica() {
        // Arrange
        String mensagem = "Erro interno";
        Exception exception = new Exception(mensagem);

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                exceptionHandler.handleGenericException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().status());
        assertTrue(response.getBody().message().contains("Erro interno do servidor"));
        assertTrue(response.getBody().message().contains(mensagem));
    }

    @Test
    @DisplayName("ErrorResponse deve ter timestamp atual")
    void errorResponseDeveTerTimestampAtual() {
        // Arrange
        BusinessException exception = new BusinessException("teste");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                exceptionHandler.handleBusinessException(exception);

        // Assert
        assertNotNull(response.getBody().timestamp());
        // Timestamp deve ser recente (dentro dos últimos 5 segundos)
        assertTrue(response.getBody().timestamp().isAfter(LocalDateTime.now().minusSeconds(5)));
        assertTrue(response.getBody().timestamp().isBefore(LocalDateTime.now().plusSeconds(5)));
    }
}


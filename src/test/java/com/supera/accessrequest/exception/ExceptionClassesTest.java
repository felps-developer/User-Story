package com.supera.accessrequest.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Exception Classes - Testes Unitários")
class ExceptionClassesTest {

    @Test
    @DisplayName("BusinessException deve ser criada com mensagem")
    void businessExceptionDeveSerCriadaComMensagem() {
        // Arrange
        String mensagem = "Erro de negócio";

        // Act
        BusinessException exception = new BusinessException(mensagem);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("BusinessException deve ser criada com mensagem e causa")
    void businessExceptionDeveSerCriadaComMensagemECausa() {
        // Arrange
        String mensagem = "Erro de negócio";
        Throwable causa = new RuntimeException("Causa original");

        // Act
        BusinessException exception = new BusinessException(mensagem, causa);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals(causa, exception.getCause());
    }

    @Test
    @DisplayName("ResourceNotFoundException deve ser criada com mensagem simples")
    void resourceNotFoundExceptionDeveSerCriadaComMensagemSimples() {
        // Arrange
        String mensagem = "Recurso não encontrado";

        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException(mensagem);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
    }

    @Test
    @DisplayName("ResourceNotFoundException deve formatar mensagem com resource, field e value")
    void resourceNotFoundExceptionDeveFormatarMensagem() {
        // Arrange
        String resource = "Usuário";
        String field = "id";
        Object value = 123L;

        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException(resource, field, value);

        // Assert
        assertNotNull(exception);
        String expectedMessage = "Usuário não encontrado(a) com id: 123";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("ResourceNotFoundException deve formatar mensagem com diferentes tipos de value")
    void resourceNotFoundExceptionDeveFormatarMensagemComDiferentesTipos() {
        // Arrange & Act
        ResourceNotFoundException exception1 = new ResourceNotFoundException("Módulo", "nome", "CRM");
        ResourceNotFoundException exception2 = new ResourceNotFoundException("Solicitação", "protocolo", "SOL-20231122-0001");

        // Assert
        assertEquals("Módulo não encontrado(a) com nome: CRM", exception1.getMessage());
        assertEquals("Solicitação não encontrado(a) com protocolo: SOL-20231122-0001", exception2.getMessage());
    }

    @Test
    @DisplayName("UnauthorizedException deve ser criada com mensagem")
    void unauthorizedExceptionDeveSerCriadaComMensagem() {
        // Arrange
        String mensagem = "Acesso não autorizado";

        // Act
        UnauthorizedException exception = new UnauthorizedException(mensagem);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
    }

    @Test
    @DisplayName("Todas as exceções devem ser RuntimeException")
    void todasExcecoesDevemSerRuntimeException() {
        // Act
        BusinessException businessEx = new BusinessException("teste");
        ResourceNotFoundException resourceEx = new ResourceNotFoundException("teste");
        UnauthorizedException unauthorizedEx = new UnauthorizedException("teste");

        // Assert
        assertTrue(businessEx instanceof RuntimeException);
        assertTrue(resourceEx instanceof RuntimeException);
        assertTrue(unauthorizedEx instanceof RuntimeException);
    }
}


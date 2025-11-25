package com.supera.accessrequest.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StatusSolicitacao - Testes Unitários")
class StatusSolicitacaoTest {

    @Test
    @DisplayName("Deve retornar todos os valores do enum")
    void deveRetornarTodosOsValoresDoEnum() {
        // Act
        StatusSolicitacao[] valores = StatusSolicitacao.values();

        // Assert
        assertNotNull(valores);
        assertEquals(3, valores.length);
        assertEquals(StatusSolicitacao.ATIVO, valores[0]);
        assertEquals(StatusSolicitacao.NEGADO, valores[1]);
        assertEquals(StatusSolicitacao.CANCELADO, valores[2]);
    }

    @Test
    @DisplayName("Deve retornar descrição correta para cada status")
    void deveRetornarDescricaoCorretaParaCadaStatus() {
        // Assert
        assertEquals("Ativo", StatusSolicitacao.ATIVO.getDescricao());
        assertEquals("Negado", StatusSolicitacao.NEGADO.getDescricao());
        assertEquals("Cancelado", StatusSolicitacao.CANCELADO.getDescricao());
    }

    @Test
    @DisplayName("Deve converter string para enum usando valueOf")
    void deveConverterStringParaEnumUsandoValueOf() {
        // Act & Assert
        assertEquals(StatusSolicitacao.ATIVO, StatusSolicitacao.valueOf("ATIVO"));
        assertEquals(StatusSolicitacao.NEGADO, StatusSolicitacao.valueOf("NEGADO"));
        assertEquals(StatusSolicitacao.CANCELADO, StatusSolicitacao.valueOf("CANCELADO"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando valueOf recebe string inválida")
    void deveLancarExcecaoQuandoValueOfRecebeStringInvalida() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            StatusSolicitacao.valueOf("INVALIDO");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            StatusSolicitacao.valueOf("ativo");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            StatusSolicitacao.valueOf("");
        });
    }
}


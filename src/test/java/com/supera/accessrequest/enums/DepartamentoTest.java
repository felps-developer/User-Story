package com.supera.accessrequest.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Departamento - Testes Unitários")
class DepartamentoTest {

    @Test
    @DisplayName("Deve retornar todos os valores do enum")
    void deveRetornarTodosOsValoresDoEnum() {
        // Act
        Departamento[] valores = Departamento.values();

        // Assert
        assertNotNull(valores);
        assertEquals(5, valores.length);
        assertEquals(Departamento.TI, valores[0]);
        assertEquals(Departamento.FINANCEIRO, valores[1]);
        assertEquals(Departamento.RH, valores[2]);
        assertEquals(Departamento.OPERACOES, valores[3]);
        assertEquals(Departamento.OUTROS, valores[4]);
    }

    @Test
    @DisplayName("Deve retornar nome correto para cada departamento")
    void deveRetornarNomeCorretoParaCadaDepartamento() {
        // Assert
        assertEquals("TI", Departamento.TI.getNome());
        assertEquals("Financeiro", Departamento.FINANCEIRO.getNome());
        assertEquals("RH", Departamento.RH.getNome());
        assertEquals("Operações", Departamento.OPERACOES.getNome());
        assertEquals("Outros", Departamento.OUTROS.getNome());
    }

    @Test
    @DisplayName("Deve converter string para enum quando existe (case-sensitive)")
    void deveConverterStringParaEnumQuandoExisteCaseSensitive() {
        // Act & Assert
        assertEquals(Departamento.TI, Departamento.fromString("TI"));
        assertEquals(Departamento.FINANCEIRO, Departamento.fromString("Financeiro"));
        assertEquals(Departamento.RH, Departamento.fromString("RH"));
        assertEquals(Departamento.OPERACOES, Departamento.fromString("Operações"));
        assertEquals(Departamento.OUTROS, Departamento.fromString("Outros"));
    }

    @Test
    @DisplayName("Deve converter string para enum quando existe (case-insensitive)")
    void deveConverterStringParaEnumQuandoExisteCaseInsensitive() {
        // Act & Assert
        assertEquals(Departamento.TI, Departamento.fromString("ti"));
        assertEquals(Departamento.TI, Departamento.fromString("Ti"));
        assertEquals(Departamento.TI, Departamento.fromString("tI"));
        assertEquals(Departamento.FINANCEIRO, Departamento.fromString("financeiro"));
        assertEquals(Departamento.FINANCEIRO, Departamento.fromString("FINANCEIRO"));
        assertEquals(Departamento.FINANCEIRO, Departamento.fromString("FinAnCeIrO"));
        assertEquals(Departamento.RH, Departamento.fromString("rh"));
        assertEquals(Departamento.RH, Departamento.fromString("Rh"));
        assertEquals(Departamento.OPERACOES, Departamento.fromString("operações"));
        assertEquals(Departamento.OPERACOES, Departamento.fromString("Operações"));
        assertEquals(Departamento.OUTROS, Departamento.fromString("outros"));
        assertEquals(Departamento.OUTROS, Departamento.fromString("OUTROS"));
    }

    @Test
    @DisplayName("Deve retornar OUTROS quando string não existe")
    void deveRetornarOutrosQuandoStringNaoExiste() {
        // Act & Assert
        assertEquals(Departamento.OUTROS, Departamento.fromString("Inexistente"));
        assertEquals(Departamento.OUTROS, Departamento.fromString(""));
        assertEquals(Departamento.OUTROS, Departamento.fromString("ABC"));
        assertEquals(Departamento.OUTROS, Departamento.fromString("123"));
        assertEquals(Departamento.OUTROS, Departamento.fromString(null));
    }

    @Test
    @DisplayName("Deve retornar OUTROS quando string é null")
    void deveRetornarOutrosQuandoStringENull() {
        // Act
        Departamento resultado = Departamento.fromString(null);

        // Assert
        assertEquals(Departamento.OUTROS, resultado);
    }

    @Test
    @DisplayName("Deve retornar OUTROS quando string está vazia")
    void deveRetornarOutrosQuandoStringEstaVazia() {
        // Act
        Departamento resultado = Departamento.fromString("");
        
        // Assert
        assertEquals(Departamento.OUTROS, resultado);
    }

    @Test
    @DisplayName("Deve testar todos os branches do loop em fromString")
    void deveTestarTodosOsBranchesDoLoopEmFromString() {
        // Act & Assert - Testar cada departamento individualmente para garantir que todos os branches do loop são executados
        assertEquals(Departamento.TI, Departamento.fromString("TI"));
        assertEquals(Departamento.FINANCEIRO, Departamento.fromString("Financeiro"));
        assertEquals(Departamento.RH, Departamento.fromString("RH"));
        assertEquals(Departamento.OPERACOES, Departamento.fromString("Operações"));
        assertEquals(Departamento.OUTROS, Departamento.fromString("Outros"));
        
        // Testar que quando nenhum corresponde, retorna OUTROS
        assertEquals(Departamento.OUTROS, Departamento.fromString("XYZ"));
    }

    @Test
    @DisplayName("Deve testar branch quando loop não encontra correspondência")
    void deveTestarBranchQuandoLoopNaoEncontraCorrespondencia() {
        // Act - Testar vários casos que não correspondem a nenhum departamento
        Departamento resultado1 = Departamento.fromString("DepartamentoInexistente");
        Departamento resultado2 = Departamento.fromString("123");
        Departamento resultado3 = Departamento.fromString("ABC");
        
        // Assert - Todos devem retornar OUTROS (branch do return após o loop)
        assertEquals(Departamento.OUTROS, resultado1);
        assertEquals(Departamento.OUTROS, resultado2);
        assertEquals(Departamento.OUTROS, resultado3);
    }
}


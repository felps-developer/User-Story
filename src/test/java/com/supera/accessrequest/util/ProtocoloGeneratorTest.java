package com.supera.accessrequest.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProtocoloGenerator - Testes Unitários")
class ProtocoloGeneratorTest {

    private final ProtocoloGenerator protocoloGenerator = new ProtocoloGenerator();

    @Test
    @DisplayName("Deve gerar protocolo com formato correto")
    void deveGerarProtocoloComFormatoCorreto() {
        // Arrange
        long sequencia = 0L;
        String dataHoje = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String expectedProtocolo = String.format("SOL-%s-0001", dataHoje);

        // Act
        String protocolo = protocoloGenerator.generate(sequencia);

        // Assert
        assertNotNull(protocolo);
        assertEquals(expectedProtocolo, protocolo);
        assertTrue(protocolo.startsWith("SOL-"));
        assertTrue(protocolo.matches("SOL-\\d{8}-\\d{4}"));
    }

    @Test
    @DisplayName("Deve gerar protocolo com sequência incrementada")
    void deveGerarProtocoloComSequenciaIncrementada() {
        // Arrange
        long sequencia = 99L;
        String dataHoje = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String expectedProtocolo = String.format("SOL-%s-0100", dataHoje);

        // Act
        String protocolo = protocoloGenerator.generate(sequencia);

        // Assert
        assertEquals(expectedProtocolo, protocolo);
        assertTrue(protocolo.endsWith("-0100"));
    }

    @Test
    @DisplayName("Deve gerar protocolos diferentes para sequências diferentes")
    void deveGerarProtocolosDiferentesParaSequenciasDiferentes() {
        // Act
        String protocolo1 = protocoloGenerator.generate(0L);
        String protocolo2 = protocoloGenerator.generate(1L);
        String protocolo3 = protocoloGenerator.generate(2L);

        // Assert
        assertNotEquals(protocolo1, protocolo2);
        assertNotEquals(protocolo2, protocolo3);
        assertNotEquals(protocolo1, protocolo3);
    }

    @Test
    @DisplayName("Deve formatar número da sequência com 4 dígitos")
    void deveFormatarNumeroComQuatroDigitos() {
        // Act
        String protocolo1 = protocoloGenerator.generate(0L);
        String protocolo2 = protocoloGenerator.generate(9L);
        String protocolo3 = protocoloGenerator.generate(99L);
        String protocolo4 = protocoloGenerator.generate(999L);

        // Assert
        assertTrue(protocolo1.endsWith("-0001"));
        assertTrue(protocolo2.endsWith("-0010"));
        assertTrue(protocolo3.endsWith("-0100"));
        assertTrue(protocolo4.endsWith("-1000"));
    }
}


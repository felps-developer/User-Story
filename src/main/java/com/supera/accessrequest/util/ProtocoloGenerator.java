package com.supera.accessrequest.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class ProtocoloGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public String generate(long sequenciaHoje) {
        String dataParte = LocalDate.now().format(FORMATTER);
        String numeroParte = String.format("%04d", sequenciaHoje + 1);
        return String.format("SOL-%s-%s", dataParte, numeroParte);
    }
}


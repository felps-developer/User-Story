package com.supera.accessrequest.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Departamento {
    TI("TI"),
    FINANCEIRO("Financeiro"),
    RH("RH"),
    OPERACOES("Operações"),
    OUTROS("Outros");

    private final String nome;

    public static Departamento fromString(String nome) {
        for (Departamento dept : Departamento.values()) {
            if (dept.getNome().equalsIgnoreCase(nome)) {
                return dept;
            }
        }
        return OUTROS;
    }
}


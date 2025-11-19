package com.supera.accessrequest.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusSolicitacao {
    ATIVO("Ativo"),
    NEGADO("Negado"),
    CANCELADO("Cancelado");

    private final String descricao;
}


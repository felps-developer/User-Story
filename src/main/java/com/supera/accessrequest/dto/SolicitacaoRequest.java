package com.supera.accessrequest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SolicitacaoRequest(
        @NotEmpty(message = "Deve selecionar pelo menos 1 módulo")
        @Size(min = 1, max = 3, message = "Deve selecionar entre 1 e 3 módulos")
        List<Long> modulosIds,

        @NotBlank(message = "Justificativa é obrigatória")
        @Size(min = 20, max = 500, message = "Justificativa deve ter entre 20 e 500 caracteres")
        String justificativa,

        @NotNull(message = "Campo urgente é obrigatório")
        Boolean urgente
) {}


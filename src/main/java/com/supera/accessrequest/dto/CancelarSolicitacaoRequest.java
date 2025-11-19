package com.supera.accessrequest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelarSolicitacaoRequest(
        @NotBlank(message = "Motivo do cancelamento é obrigatório")
        @Size(min = 10, max = 200, message = "Motivo deve ter entre 10 e 200 caracteres")
        String motivo
) {}


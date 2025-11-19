package com.supera.accessrequest.dto;

import com.supera.accessrequest.entity.Solicitacao;
import com.supera.accessrequest.entity.SolicitacaoModulo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record SolicitacaoResponse(
        Long id,
        String protocolo,
        List<ModuloSimpleResponse> modulos,
        String status,
        String justificativa,
        Boolean urgente,
        String motivoNegacao,
        String motivoCancelamento,
        LocalDateTime dataSolicitacao,
        LocalDateTime dataExpiracao,
        Long usuarioId,
        String usuarioNome,
        String departamento
) {
    public static SolicitacaoResponse fromEntity(Solicitacao solicitacao) {
        return new SolicitacaoResponse(
                solicitacao.getId(),
                solicitacao.getProtocolo(),
                solicitacao.getModulos().stream()
                        .map(sm -> new ModuloSimpleResponse(
                                sm.getModulo().getId(),
                                sm.getModulo().getNome()
                        ))
                        .collect(Collectors.toList()),
                solicitacao.getStatus().name(),
                solicitacao.getJustificativa(),
                solicitacao.getUrgente(),
                solicitacao.getMotivoNegacao(),
                solicitacao.getMotivoCancelamento(),
                solicitacao.getDataSolicitacao(),
                solicitacao.getDataExpiracao(),
                solicitacao.getUsuario().getId(),
                solicitacao.getUsuario().getNome(),
                solicitacao.getUsuario().getDepartamento().name()
        );
    }

    public record ModuloSimpleResponse(Long id, String nome) {}
}


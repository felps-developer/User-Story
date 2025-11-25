package com.supera.accessrequest.dto;

import com.supera.accessrequest.entity.Solicitacao;

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
        String departamento,
        List<HistoricoResponse> historico
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
                solicitacao.getUsuario().getDepartamento().name(),
                solicitacao.getHistorico().stream()
                        .map(h -> new HistoricoResponse(
                                h.getId(),
                                h.getAcao(),
                                h.getDescricao(),
                                h.getUsuario().getNome(),
                                h.getDataAcao()
                        ))
                        .sorted((h1, h2) -> h2.dataAcao().compareTo(h1.dataAcao())) // Mais recentes primeiro
                        .collect(Collectors.toList())
        );
    }

    public record ModuloSimpleResponse(Long id, String nome) {}
    
    public record HistoricoResponse(
            Long id,
            String acao,
            String descricao,
            String usuarioNome,
            LocalDateTime dataAcao
    ) {}
}


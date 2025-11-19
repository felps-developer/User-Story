package com.supera.accessrequest.dto;

import com.supera.accessrequest.entity.Modulo;
import com.supera.accessrequest.entity.ModuloDepartamento;
import com.supera.accessrequest.entity.ModuloIncompativel;

import java.util.List;
import java.util.stream.Collectors;

public record ModuloResponse(
        Long id,
        String nome,
        String descricao,
        Boolean ativo,
        List<String> departamentosPermitidos,
        List<ModuloIncompativelResponse> modulosIncompativeis
) {
    public static ModuloResponse fromEntity(Modulo modulo) {
        return new ModuloResponse(
                modulo.getId(),
                modulo.getNome(),
                modulo.getDescricao(),
                modulo.getAtivo(),
                modulo.getDepartamentosPermitidos().stream()
                        .map(md -> md.getDepartamento().name())
                        .collect(Collectors.toList()),
                modulo.getModulosIncompativeis().stream()
                        .map(mi -> new ModuloIncompativelResponse(
                                mi.getModuloIncompativel().getId(),
                                mi.getModuloIncompativel().getNome()
                        ))
                        .collect(Collectors.toList())
        );
    }

    public record ModuloIncompativelResponse(Long id, String nome) {}
}


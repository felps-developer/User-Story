package com.supera.accessrequest.dto;

public record LoginResponse(
        String token,
        String tipo,
        Long usuarioId,
        String nome,
        String email,
        String departamento
) {
    public LoginResponse(String token, Long usuarioId, String nome, String email, String departamento) {
        this(token, "Bearer", usuarioId, nome, email, departamento);
    }
}


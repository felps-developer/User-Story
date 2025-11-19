package com.supera.accessrequest.controller;

import com.supera.accessrequest.dto.ModuloResponse;
import com.supera.accessrequest.service.ModuloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/modulos")
@RequiredArgsConstructor
@Tag(name = "Módulos", description = "Endpoints para consulta de módulos disponíveis")
@SecurityRequirement(name = "Bearer Authentication")
public class ModuloController {

    private final ModuloService moduloService;

    @GetMapping
    @Operation(summary = "Listar módulos disponíveis", 
               description = "Retorna todos os módulos ativos com informações de departamentos permitidos e incompatibilidades")
    public ResponseEntity<List<ModuloResponse>> listarModulosDisponiveis() {
        List<ModuloResponse> modulos = moduloService.listarModulosDisponiveis();
        return ResponseEntity.ok(modulos);
    }
}


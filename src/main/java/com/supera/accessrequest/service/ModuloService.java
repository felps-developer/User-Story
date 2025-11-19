package com.supera.accessrequest.service;

import com.supera.accessrequest.dto.ModuloResponse;
import com.supera.accessrequest.entity.Modulo;
import com.supera.accessrequest.repository.ModuloRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuloService {

    private final ModuloRepository moduloRepository;

    @Transactional(readOnly = true)
    public List<ModuloResponse> listarModulosDisponiveis() {
        log.info("Listando todos os módulos disponíveis");
        
        List<Modulo> modulos = moduloRepository.findByAtivoTrue();
        
        return modulos.stream()
                .map(ModuloResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Modulo buscarPorId(Long id) {
        return moduloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Módulo não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Modulo> buscarPorIds(List<Long> ids) {
        List<Modulo> modulos = moduloRepository.findAllByIdsAndAtivoTrue(ids);
        
        if (modulos.size() != ids.size()) {
            throw new RuntimeException("Um ou mais módulos não foram encontrados ou estão inativos");
        }
        
        return modulos;
    }
}


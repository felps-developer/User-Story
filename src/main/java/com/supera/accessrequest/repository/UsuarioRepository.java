package com.supera.accessrequest.repository;

import com.supera.accessrequest.entity.Usuario;
import com.supera.accessrequest.enums.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    Optional<Usuario> findByEmailAndAtivoTrue(String email);
    
    boolean existsByEmail(String email);
    
    long countByDepartamento(Departamento departamento);
}


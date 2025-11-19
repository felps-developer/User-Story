package com.supera.accessrequest.repository;

import com.supera.accessrequest.entity.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {
    
    List<Modulo> findByAtivoTrue();
    
    Optional<Modulo> findByIdAndAtivoTrue(Long id);
    
    @Query("SELECT m FROM Modulo m WHERE m.id IN :ids AND m.ativo = true")
    List<Modulo> findAllByIdsAndAtivoTrue(@Param("ids") List<Long> ids);
    
    boolean existsByNome(String nome);
}


package com.supera.accessrequest.repository;

import com.supera.accessrequest.entity.Modulo;
import com.supera.accessrequest.entity.ModuloDepartamento;
import com.supera.accessrequest.enums.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuloDepartamentoRepository extends JpaRepository<ModuloDepartamento, Long> {
    
    List<ModuloDepartamento> findByModuloId(Long moduloId);
    
    List<ModuloDepartamento> findByDepartamento(Departamento departamento);
    
    @Query("SELECT md.modulo FROM ModuloDepartamento md WHERE md.departamento = :departamento AND md.modulo.ativo = true")
    List<Modulo> findModulosByDepartamento(@Param("departamento") Departamento departamento);
    
    @Query("SELECT CASE WHEN COUNT(md) > 0 THEN true ELSE false END " +
           "FROM ModuloDepartamento md " +
           "WHERE md.modulo.id = :moduloId AND md.departamento = :departamento")
    boolean existsByModuloIdAndDepartamento(@Param("moduloId") Long moduloId, 
                                           @Param("departamento") Departamento departamento);
}


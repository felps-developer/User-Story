package com.supera.accessrequest.repository;

import com.supera.accessrequest.entity.ModuloIncompativel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuloIncompativelRepository extends JpaRepository<ModuloIncompativel, Long> {
    
    List<ModuloIncompativel> findByModuloId(Long moduloId);
    
    @Query("SELECT mi.moduloIncompativel.id FROM ModuloIncompativel mi WHERE mi.modulo.id = :moduloId")
    List<Long> findModulosIncompatibilidadesByModuloId(@Param("moduloId") Long moduloId);
    
    @Query("SELECT CASE WHEN COUNT(mi) > 0 THEN true ELSE false END " +
           "FROM ModuloIncompativel mi " +
           "WHERE mi.modulo.id = :moduloId AND mi.moduloIncompativel.id = :moduloIncompativelId")
    boolean existsByModuloIdAndModuloIncompativelId(@Param("moduloId") Long moduloId, 
                                                    @Param("moduloIncompativelId") Long moduloIncompativelId);
}


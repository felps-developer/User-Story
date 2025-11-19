package com.supera.accessrequest.repository;

import com.supera.accessrequest.entity.AcessoUsuarioModulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AcessoUsuarioModuloRepository extends JpaRepository<AcessoUsuarioModulo, Long> {
    
    List<AcessoUsuarioModulo> findByUsuarioIdAndAtivoTrue(Long usuarioId);
    
    List<AcessoUsuarioModulo> findBySolicitacaoIdAndAtivoTrue(Long solicitacaoId);
    
    @Query("SELECT COUNT(a) FROM AcessoUsuarioModulo a " +
           "WHERE a.usuario.id = :usuarioId AND a.ativo = true")
    long countByUsuarioIdAndAtivoTrue(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT a.modulo.id FROM AcessoUsuarioModulo a " +
           "WHERE a.usuario.id = :usuarioId AND a.ativo = true")
    List<Long> findModuloIdsAtivosByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM AcessoUsuarioModulo a " +
           "WHERE a.usuario.id = :usuarioId " +
           "AND a.modulo.id = :moduloId " +
           "AND a.ativo = true")
    boolean existsByUsuarioIdAndModuloIdAndAtivoTrue(
            @Param("usuarioId") Long usuarioId,
            @Param("moduloId") Long moduloId
    );
    
    @Query("SELECT a FROM AcessoUsuarioModulo a " +
           "WHERE a.dataExpiracao <= :dataLimite AND a.ativo = true")
    List<AcessoUsuarioModulo> findAcessosProximosExpiracao(@Param("dataLimite") LocalDateTime dataLimite);
    
    @Modifying
    @Query("UPDATE AcessoUsuarioModulo a SET a.ativo = false " +
           "WHERE a.solicitacao.id = :solicitacaoId AND a.ativo = true")
    void desativarAcessosPorSolicitacaoId(@Param("solicitacaoId") Long solicitacaoId);
}


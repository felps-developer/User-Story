package com.supera.accessrequest.repository;

import com.supera.accessrequest.entity.Solicitacao;
import com.supera.accessrequest.enums.StatusSolicitacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {
    
    Optional<Solicitacao> findByProtocolo(String protocolo);
    
    Page<Solicitacao> findByUsuarioId(Long usuarioId, Pageable pageable);
    
    Page<Solicitacao> findByUsuarioIdAndStatus(Long usuarioId, StatusSolicitacao status, Pageable pageable);
    
    @Query(value = "SELECT DISTINCT s.* FROM solicitacao s " +
           "LEFT JOIN solicitacao_modulo sm ON s.id = sm.solicitacao_id " +
           "LEFT JOIN modulo m ON sm.modulo_id = m.id " +
           "WHERE s.usuario_id = :usuarioId " +
           "AND (:status IS NULL OR s.status = CAST(:status AS VARCHAR)) " +
           "AND (:urgente IS NULL OR s.urgente = :urgente) " +
           "AND (CAST(:dataInicio AS TIMESTAMP) IS NULL OR s.data_solicitacao >= CAST(:dataInicio AS TIMESTAMP)) " +
           "AND (CAST(:dataFim AS TIMESTAMP) IS NULL OR s.data_solicitacao <= CAST(:dataFim AS TIMESTAMP)) " +
           "AND (:pesquisa IS NULL OR s.protocolo ILIKE CONCAT('%', :pesquisa, '%') OR m.nome ILIKE CONCAT('%', :pesquisa, '%'))",
           nativeQuery = true)
    Page<Solicitacao> findByUsuarioIdWithFilters(
            @Param("usuarioId") Long usuarioId,
            @Param("status") String status,
            @Param("urgente") Boolean urgente,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("pesquisa") String pesquisa,
            Pageable pageable
    );
    
    @Query("SELECT CASE WHEN COUNT(sm) > 0 THEN true ELSE false END " +
           "FROM Solicitacao s JOIN s.modulos sm " +
           "WHERE s.usuario.id = :usuarioId " +
           "AND sm.modulo.id = :moduloId " +
           "AND s.status = :status")
    boolean existsByUsuarioIdAndModuloIdAndStatus(
            @Param("usuarioId") Long usuarioId,
            @Param("moduloId") Long moduloId,
            @Param("status") StatusSolicitacao status
    );
    
    @Query(value = "SELECT COUNT(*) FROM solicitacao " +
           "WHERE DATE(data_solicitacao) = CURRENT_DATE", 
           nativeQuery = true)
    long countSolicitacoesHoje();
}


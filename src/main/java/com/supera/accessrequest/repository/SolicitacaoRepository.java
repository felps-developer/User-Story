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
    
    @Query("SELECT s FROM Solicitacao s " +
           "WHERE s.usuario.id = :usuarioId " +
           "AND (:status IS NULL OR s.status = :status) " +
           "AND (:urgente IS NULL OR s.urgente = :urgente) " +
           "AND (:dataInicio IS NULL OR s.dataSolicitacao >= :dataInicio) " +
           "AND (:dataFim IS NULL OR s.dataSolicitacao <= :dataFim) " +
           "AND (:pesquisa IS NULL OR LOWER(s.protocolo) LIKE LOWER(CONCAT('%', :pesquisa, '%')))")
    Page<Solicitacao> findByUsuarioIdWithFilters(
            @Param("usuarioId") Long usuarioId,
            @Param("status") StatusSolicitacao status,
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
    
    @Query("SELECT COUNT(s) FROM Solicitacao s " +
           "WHERE DATE(s.dataSolicitacao) = CURRENT_DATE")
    long countSolicitacoesHoje();
}


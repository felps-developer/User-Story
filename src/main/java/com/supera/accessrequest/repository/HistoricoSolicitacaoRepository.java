package com.supera.accessrequest.repository;

import com.supera.accessrequest.entity.HistoricoSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoSolicitacaoRepository extends JpaRepository<HistoricoSolicitacao, Long> {
    
    List<HistoricoSolicitacao> findBySolicitacaoIdOrderByDataAcaoDesc(Long solicitacaoId);
    
    List<HistoricoSolicitacao> findByUsuarioId(Long usuarioId);
}


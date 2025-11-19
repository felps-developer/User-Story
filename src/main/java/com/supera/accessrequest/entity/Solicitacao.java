package com.supera.accessrequest.entity;

import com.supera.accessrequest.enums.StatusSolicitacao;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "solicitacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String protocolo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String justificativa;

    @Column(nullable = false)
    private Boolean urgente = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusSolicitacao status;

    @Column(columnDefinition = "TEXT")
    private String motivoNegacao;

    @Column(columnDefinition = "TEXT")
    private String motivoCancelamento;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column(name = "data_expiracao")
    private LocalDateTime dataExpiracao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitacao_origem_id")
    private Solicitacao solicitacaoOrigem;

    @OneToMany(mappedBy = "solicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<SolicitacaoModulo> modulos = new HashSet<>();

    @OneToMany(mappedBy = "solicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<HistoricoSolicitacao> historico = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper methods
    public void addModulo(Modulo modulo) {
        SolicitacaoModulo solicitacaoModulo = SolicitacaoModulo.builder()
                .solicitacao(this)
                .modulo(modulo)
                .build();
        this.modulos.add(solicitacaoModulo);
    }

    public void addHistorico(String acao, String descricao, Usuario usuario) {
        HistoricoSolicitacao hist = HistoricoSolicitacao.builder()
                .solicitacao(this)
                .acao(acao)
                .descricao(descricao)
                .usuario(usuario)
                .dataAcao(LocalDateTime.now())
                .build();
        this.historico.add(hist);
    }
}


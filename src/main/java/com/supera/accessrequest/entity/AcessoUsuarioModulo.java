package com.supera.accessrequest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "acesso_usuario_modulo", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"usuario_id", "modulo_id", "solicitacao_id"})
}, indexes = {
    @Index(name = "idx_usuario_ativo", columnList = "usuario_id, ativo"),
    @Index(name = "idx_expiracao_ativo", columnList = "data_expiracao, ativo")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcessoUsuarioModulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modulo_id", nullable = false)
    private Modulo modulo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitacao_id", nullable = false)
    private Solicitacao solicitacao;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_expiracao", nullable = false)
    private LocalDateTime dataExpiracao;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;
}


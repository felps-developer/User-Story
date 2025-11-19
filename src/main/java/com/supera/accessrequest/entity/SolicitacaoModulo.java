package com.supera.accessrequest.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "solicitacao_modulo", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"solicitacao_id", "modulo_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitacaoModulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitacao_id", nullable = false)
    private Solicitacao solicitacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modulo_id", nullable = false)
    private Modulo modulo;
}


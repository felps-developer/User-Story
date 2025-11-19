package com.supera.accessrequest.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "modulo_incompativel", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"modulo_id", "modulo_incompativel_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuloIncompativel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modulo_id", nullable = false)
    private Modulo modulo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modulo_incompativel_id", nullable = false)
    private Modulo moduloIncompativel;
}


package com.supera.accessrequest.entity;

import com.supera.accessrequest.enums.Departamento;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "modulo_departamento", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"modulo_id", "departamento"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuloDepartamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modulo_id", nullable = false)
    private Modulo modulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Departamento departamento;
}


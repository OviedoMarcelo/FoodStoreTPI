package com.tup.programacion3.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
/**
 * Clase base para todas las entidades del proyecto.
 * @MappedSuperclass indica que esta clase no genera tabla propia,
 * pero sus campos (id, eliminado, createdAt) se heredan
 * a la tabla de cada entidad hija.
 */
@MappedSuperclass //
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true) //Coloco esto para indicar que voy a indicar que campos quiero.
@SuperBuilder
@NoArgsConstructor

public class Base {
    @EqualsAndHashCode.Include //Solo el campo id para el equal y hash
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    private boolean eliminado = false;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}

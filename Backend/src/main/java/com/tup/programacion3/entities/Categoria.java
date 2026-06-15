package com.tup.programacion3.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true) //Para respetar el uso del id de base
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table (name = "categorias")
public class Categoria extends Base {
    private String categoria;
    private String descripcion;

}

package com.tup.programacion3.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table (name="productos")
public class Producto extends Base {
    private String nombre;
    private double precio;
    private String descripcion;
    private int stock;
    @ManyToOne(fetch = FetchType.EAGER) //Cambiamos esto para que funcione el listar producto mostrando la categoría
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
    private String imagen;
    @Builder.Default //Si no pongo esto se inicializaa en false
    private boolean disponible = true;


}

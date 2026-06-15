package com.tup.programacion3.entities;

import com.tup.programacion3.enums.Rol;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true) //Para respetar el uso del id de base
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "usuarios")
public class Usuario extends Base {
    private String nombre;
    private String apellido;
    private String mail;
    private String celular;
    private String contrasena;
    @Enumerated(EnumType.STRING)
    private Rol rol;
    @ToString.Exclude
    @Builder.Default
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Set<Pedido> pedidos = new HashSet<>();

}

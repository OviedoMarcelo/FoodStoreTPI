package com.tup.programacion3.dtos;

import com.tup.programacion3.entities.Pedido;

import java.util.Set;

public record UsuarioDTO(
        String nombre,
        String apellido,
        String mail,
        String celular,
        Set<Pedido> pedidos
) {
    //Sobreescribo el método para limitar lo que muestro
    @Override
    public String toString() {
        return "UsuarioDTO{" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", mail='" + mail + '\'' +
                ", celular='" + celular + '\'' +
                ", cantidad de pedidos=" + pedidos.size() +
                '}';
    }
}

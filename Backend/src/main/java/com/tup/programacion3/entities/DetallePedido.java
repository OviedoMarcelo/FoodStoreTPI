package com.tup.programacion3.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@SuperBuilder
@NoArgsConstructor

@Entity
@Table(name = "detalle_pedidos")
public class DetallePedido extends Base {
    private int cantidad;
    private double subtotal;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.subtotal = producto.getPrecio() * cantidad; // ← lógica especial
    }

}

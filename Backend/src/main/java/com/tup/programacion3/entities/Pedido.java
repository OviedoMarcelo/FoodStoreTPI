package com.tup.programacion3.entities;

import com.tup.programacion3.enums.Estado;
import com.tup.programacion3.enums.FormaPago;
import com.tup.programacion3.interfaces.Calculable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


//Lombok
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)//Para respetar el uso del id de base
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor

@Entity
@Table(name = "pedidos")
public class Pedido extends Base implements Calculable {
    private LocalDate fecha;
    @Enumerated(EnumType.STRING)
    private Estado estado;
    private double total;
    @Enumerated(EnumType.STRING)
    private FormaPago formaPago;
    @ToString.Exclude
    @Builder.Default
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Set<DetallePedido> detallePedidos = new HashSet<>();

    /**
     * Método factory estático para crear un Pedido.
     * Reemplaza el constructor manual porque @SuperBuilder (heredado de Base)
     * genera su propio mecanismo de construcción interno y no se lleva bien
     * con constructores que llaman a super() manualmente — puede causar
     * que los campos heredados (id, eliminado, createdAt) no se inicialicen
     * correctamente o generar errores de compilación por ambigüedad.
     */
    public static Pedido crear(FormaPago formaPago, Usuario usuario) {
        Pedido pedido = Pedido.builder()
                .fecha(LocalDate.now())
                .total(0.0)
                .formaPago(formaPago)
                .estado(Estado.PENDIENTE)
                .build();
        usuario.getPedidos().add(pedido);
        return pedido;
    }


    @Override
    public void calcularTotal() {
        this.total = detallePedidos.stream()
                .mapToDouble(DetallePedido::getSubtotal)
                .sum();
    }

    //Métodos particulares

    public void addDetallePedido(int cantidad, Producto producto) {
        DetallePedido detalleToAdd = DetallePedido.builder()
                .producto(producto)
                .cantidad(cantidad)
                .subtotal(producto.getPrecio() * cantidad)
                .build();
        detallePedidos.add(detalleToAdd);
        calcularTotal();
    }

    public DetallePedido findDetallePedidoByProducto(Producto producto) {
        return detallePedidos.stream()
                .filter(d -> d.getProducto().equals(producto)) //filtra los que coinciden
                .findFirst()//Cuando encuentra el primero se detiene
                .orElse(null); //Si no encuentra nada devuelve null
    }

    public void deleteDetallePedidoByProducto(Producto producto) {
        detallePedidos.remove(findDetallePedidoByProducto(producto));
        calcularTotal();
    }

}

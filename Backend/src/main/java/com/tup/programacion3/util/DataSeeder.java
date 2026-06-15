package com.tup.programacion3.util;

import com.tup.programacion3.entities.*;
import com.tup.programacion3.enums.FormaPago;
import com.tup.programacion3.enums.Rol;

import java.util.List;

public class DataSeeder {

    public static List<Categoria> crearCategorias() {
        Categoria pizzas = Categoria.builder()
                .categoria("Pizzas")
                .descripcion("Pizzas de estilo Napolitanas")
                .build();
        Categoria panchos = Categoria.builder()
                .categoria("Panchos")
                .descripcion("Super panchos de salchicha alemana")
                .build();
        Categoria bebidas = Categoria.builder()
                .categoria("Bebidas")
                .descripcion("Bebidas refrescantes")
                .build();
        return List.of(pizzas, panchos, bebidas);
    }

    public static List<Producto> crearProductos(List<Categoria> categorias) {
        Categoria pizzas  = categorias.get(0);
        Categoria panchos = categorias.get(1);
        Categoria bebidas = categorias.get(2);

        return List.of(
                // Pizzas
                Producto.builder().nombre("Muzzarella Especial").precio(1200).descripcion("Mucha muzzarella y aceitunas verdes").stock(2).categoria(pizzas).imagen("muzza.png").disponible(true).build(),
                Producto.builder().nombre("Fugazzeta").precio(1300).descripcion("Cebolla caramelizada y queso").stock(15).categoria(pizzas).imagen("fuga.png").disponible(true).build(),
                Producto.builder().nombre("Calzone").precio(1500).descripcion("Relleno de jamón, queso y huevo").stock(3).categoria(pizzas).imagen("calzone.jpg").disponible(false).build(),
                // Panchos
                Producto.builder().nombre("Pancho Alemán Clásico").precio(900).descripcion("Salchicha alemana con lluvia de papas").stock(30).categoria(panchos).imagen("pancho-clasico.png").disponible(true).build(),
                Producto.builder().nombre("Pancho con Cheddar").precio(1100).descripcion("Salchicha alemana, cheddar y bacon").stock(25).categoria(panchos).imagen("pancho-cheddar.png").disponible(true).build(),
                Producto.builder().nombre("Pancho Picante").precio(1050).descripcion("Salchicha alemana con salsa criolla y picante").stock(4).categoria(panchos).imagen("pancho-hot.png").disponible(false).build(),
                // Bebidas
                Producto.builder().nombre("Agua Mineral 500ml").precio(800).descripcion("Agua mineral sin gas").stock(50).categoria(bebidas).imagen("agua.png").disponible(true).build(),
                Producto.builder().nombre("Cerveza Rubia").precio(2500).descripcion("Pinta de cerveza artesanal").stock(4).categoria(bebidas).imagen("cerveza.jpg").disponible(false).build(),
                Producto.builder().nombre("Limonada con Menta").precio(1400).descripcion("Limonada casera fresca").stock(20).categoria(bebidas).imagen("limonada.png").disponible(true).build()
        );
    }

    public static List<Usuario> crearUsuariosConPedidos(List<Producto> productos) {
        // Índices: pizzas 0-2 | panchos 3-5 | bebidas 6-8
        Producto muzzarella = productos.get(0);
        Producto calzone = productos.get(2);
        Producto panchoClasico = productos.get(3);
        Producto panchoCheddar = productos.get(4);
        Producto agua = productos.get(6);
        Producto cerveza = productos.get(7);

        Usuario usuario1 = Usuario.builder()
                .nombre("Marcelo")
                .apellido("Oviedo")
                .mail("mail@gmail.com")
                .celular("1158503206")
                .contrasena("ABC123")
                .rol(Rol.ADMIN)
                .build();

        Usuario usuario2 = Usuario.builder()
                .nombre("Juan")
                .apellido("Ortiz")
                .mail("juancito@gmail.com")
                .celular("1158503206")
                .contrasena("ABC123")
                .rol(Rol.USUARIO)
                .build();

        // Pedidos de usuario1
        Pedido pedido1 = Pedido.crear(FormaPago.TARJETA, usuario1);
        pedido1.addDetallePedido(4, panchoClasico);
        pedido1.addDetallePedido(1, agua);

        Pedido pedido3 = Pedido.crear(FormaPago.EFECTIVO, usuario1);
        pedido3.addDetallePedido(1, panchoCheddar);
        pedido3.addDetallePedido(5, muzzarella);

        // Pedido de usuario2
        Pedido pedido2 = Pedido.crear(FormaPago.TRANSFERENCIA, usuario2);
        pedido2.addDetallePedido(3, calzone);
        pedido2.addDetallePedido(5, cerveza);

        return List.of(usuario1, usuario2);
    }
}
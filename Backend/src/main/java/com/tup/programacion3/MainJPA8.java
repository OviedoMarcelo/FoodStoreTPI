package com.tup.programacion3;

import com.tup.programacion3.entities.*;
import com.tup.programacion3.util.DataSeeder;
import jakarta.persistence.*;


import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainJPA8 {
    public static void main(String[] args) throws SQLException {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("programacion3UTN");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        org.h2.tools.Server webServer = org.h2.tools.Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082");
        webServer.start();
        System.out.println("H2 Console: http://localhost:8082");

        try {
            tx.begin();

            // 1. Crear objetos con DataSeeder
            //Clase de util para generar clases de pruebas

            List<Categoria> categorias = DataSeeder.crearCategorias();
            List<Producto> productos = DataSeeder.crearProductos(categorias);
            Set<Producto> todosLosProductos = new HashSet<>(productos);
            List<Usuario> usuarios = DataSeeder.crearUsuariosConPedidos(productos);

            // 2. Persistir categorias, productos y usuarios
            categorias.forEach(em::persist);
            productos.forEach(em::persist);
            usuarios.forEach(em::persist);
            tx.commit();

            // 3. Buscar usuario por id
            Usuario usuarioBuscar = usuarios.get(0);
            Usuario usuarioEncontrado = em.find(Usuario.class, usuarioBuscar.getId());
            if (usuarioEncontrado != null) {
                System.out.println("Usuario encontrado por id: " + usuarioEncontrado);
            } else {
                System.out.println("No se encontró usuario con ese id.");
            }
            // 4. Buscar usuario por mail
            Usuario usuarioEncontradoPorMail;
            try {
                usuarioEncontradoPorMail = em.createQuery("SELECT u FROM Usuario u WHERE u.mail = :mail", Usuario.class)
                        .setParameter("mail", "juancito@gmail.com")
                        .getSingleResult();
                System.out.println("Usuario encontrado por mail: " + usuarioEncontradoPorMail);
            } catch (NoResultException e) {
                System.out.println("No se encontró usuario con ese mail.");
            }
            // 5. Actualizar 2 productos
            tx.begin();
            System.out.println("Se actualizan 2 productos, se agrega un 10% de aumento al precio final ");
            Producto productoAct1 = em.find(Producto.class, 1L);
            Producto productoAct2 = em.find(Producto.class, 2L);
            productoAct1.setPrecio(productoAct1.getPrecio() * 1.1);
            productoAct2.setPrecio(productoAct2.getPrecio() * 1.1);
            tx.commit();
            // 6. Borrar 1 producto
            // Borrado lógico (recomendado): mantiene integridad referencial e historial
            tx.begin();
            System.out.println("Se elimina lógicamente el producto con ID: 7");
            Producto productoEliminarLogico = em.find(Producto.class, 7L);
            productoEliminarLogico.setEliminado(true);
            tx.commit();

            // Borrado físico (no recomendado en sistemas reales): elimina el registro de la BD
            // Solo es posible en productos sin detalles asociados, de lo contrario rompe la integridad referencial
            tx.begin();
            System.out.println("Se elimina físicamente el producto con ID: 9");
            Producto productoEliminarFisico = em.find(Producto.class, 9L);
            em.remove(productoEliminarFisico);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
            //webServer.stop();
        }

    }

}

package com.tup.programacion3.repository;

import com.tup.programacion3.entities.Producto;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ProductoRepository extends BaseRepository<Producto> {
    //Constructor
    public ProductoRepository() {
        super(Producto.class);
    }

    public List<Producto> buscarPorCategoria(Long categoriaId) {
        EntityManager em = emf.createEntityManager();
        try {
            // Retorna productos activos de una categoria especifica
            // filtra por categoria.id y eliminado = false usando parametro nombrado
            String jpql = "SELECT p FROM Producto p WHERE p.categoria.id = :categoriaId AND p.eliminado = false";
            return em.createQuery(jpql, Producto.class)
                    .setParameter("categoriaId", categoriaId)
                    .getResultList();
        } finally {
            em.close();
        }
    }


}

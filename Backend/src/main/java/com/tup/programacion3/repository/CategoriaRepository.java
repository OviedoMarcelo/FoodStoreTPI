package com.tup.programacion3.repository;

import com.tup.programacion3.entities.Categoria;
import com.tup.programacion3.entities.Producto;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CategoriaRepository extends BaseRepository<Categoria> {

    public CategoriaRepository() {
        super(Categoria.class);
    }

    public List<Producto> buscarProductosPorCategoria(Long categoriaId) {
        EntityManager em = emf.createEntityManager();
        try {
            // Consulta JPQL: retorna los productos activos de una categoría.
            // Como la relación es unidireccional y Categoria es la dueña, se navega
            // desde Categoria hacia su colección c.productos mediante JOIN.
            // Se filtra por el id de la categoría (:catId) y por p.eliminado = false.
            String jpql = "SELECT p FROM Categoria c JOIN c.productos p " +
                          "WHERE c.id = :catId AND p.eliminado = false";
            return em.createQuery(jpql, Producto.class)
                    .setParameter("catId", categoriaId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}

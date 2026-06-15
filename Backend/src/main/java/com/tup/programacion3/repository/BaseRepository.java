package com.tup.programacion3.repository;

import com.tup.programacion3.entities.Base;
import com.tup.programacion3.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T> {

    // Guarda la clase concreta (ej: Categoria.class, Producto.class)
    // La necesito para los find() y las queries JPQL en tiempo de ejecucion
    private final Class<T> entityClass;

    // Factory compartido via singleton JPAUtil — costoso de crear, se reutiliza
    // protected para que CategoriaRepository y ProductoRepository puedan accederlo
    protected final EntityManagerFactory emf;

    // El constructor recibe la clase concreta porque Java borra los genericos
    // en tiempo de ejecucion (type erasure) — sin esto no sabria con que clase trabajar
    public BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.emf = JPAUtil.getEntityManagerFactory();
    }

    //Guardar elemento
    public T guardar(T entity) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            T result = em.merge(entity);
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;  // relanza la excepcion para que el llamador sepa que falló
        } finally {
            em.close();  // se ejecuta SIEMPRE, con o sin error
        }
    }

    //Búsqueda por ID
    public Optional<T> buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(entityClass, id));
        } finally {
            em.close();
        }
    }

    //Listar activos
    public List<T> listarActivos() {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e.eliminado = false"; //Importante los espacios
            return em.createQuery(jpql, entityClass).getResultList();
        } finally {
            em.close();
        }
    }

    //Borrado lógico
    public boolean eliminarLogico(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            T entity = em.find(entityClass, id);
            if ((entity == null)){
                em.getTransaction().rollback();
                return false;
            }
            if (((Base)entity).isEliminado()){
                em.getTransaction().rollback();
                return false;
            }
            ((Base) entity).setEliminado(true);
            em.merge(entity);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }


}


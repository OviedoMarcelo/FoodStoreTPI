package com.tup.programacion3.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    // Una sola instancia para toda la app
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("programacion3UTN");

    // Todos piden el mismo factory por acá
    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    // Se llama al cerrar la app
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
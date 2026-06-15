package com.tup.programacion3.repository;

import com.tup.programacion3.entities.Categoria;

public class CategoriaRepository extends BaseRepository<Categoria>{
    public CategoriaRepository() {
        super(Categoria.class);
    }
}

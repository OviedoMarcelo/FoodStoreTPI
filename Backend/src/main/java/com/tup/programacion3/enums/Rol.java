package com.tup.programacion3.enums;

public enum Rol {
    ADMIN("Administrador"),
    USUARIO("Usuario");

    private final String descripcion;

    private Rol(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

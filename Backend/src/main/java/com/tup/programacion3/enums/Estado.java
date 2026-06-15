package com.tup.programacion3.enums;

public enum Estado {
    PENDIENTE("Pendiente"),
    CONFIRMADO("Confirmado"),
    TERMINADO("Terminado"),
    CANCELADO("Cancelado");

    private final String descripcion;

    Estado(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
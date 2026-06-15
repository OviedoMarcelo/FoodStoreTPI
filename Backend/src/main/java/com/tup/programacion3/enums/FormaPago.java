package com.tup.programacion3.enums;

public enum FormaPago {
    TARJETA("Tarjeta"),
    TRANSFERENCIA("Transferencia"),
    EFECTIVO("Efectivo");

    private final String descripcion;

    private FormaPago(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

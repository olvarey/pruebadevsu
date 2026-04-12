package com.devsu.customerservice.domain.model;

import com.devsu.customerservice.domain.exception.DomainException;

public class Persona {

    private final String nombre;
    private final String genero;
    private final Integer edad;
    private final String identificacion;
    private final String direccion;
    private final String telefono;

    protected Persona(
            String nombre,
            String genero,
            Integer edad,
            String identificacion,
            String direccion,
            String telefono
    ) {
        this.nombre = requireText(nombre, "nombre");
        this.genero = normalizeOptional(genero);
        this.edad = edad;
        this.identificacion = requireText(identificacion, "identificacion");
        this.direccion = requireText(direccion, "direccion");
        this.telefono = requireText(telefono, "telefono");
    }

    protected static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new DomainException(field + " es requerido");
        }
        return value.trim();
    }

    protected static String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public String nombre() {
        return nombre;
    }

    public String genero() {
        return genero;
    }

    public Integer edad() {
        return edad;
    }

    public String identificacion() {
        return identificacion;
    }

    public String direccion() {
        return direccion;
    }

    public String telefono() {
        return telefono;
    }
}

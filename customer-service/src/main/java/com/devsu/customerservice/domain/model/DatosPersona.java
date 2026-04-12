package com.devsu.customerservice.domain.model;

import com.devsu.customerservice.domain.exception.DomainException;

/** Personal data required to create a customer. */
public record DatosPersona(
    String nombre,
    String genero,
    Integer edad,
    String identificacion,
    String direccion,
    String telefono) {

  /** Creates personal data and validates the required fields. */
  public DatosPersona {
    nombre = requireText(nombre, "nombre");
    genero = normalizeOptional(genero);
    identificacion = requireText(identificacion, "identificacion");
    direccion = requireText(direccion, "direccion");
    telefono = requireText(telefono, "telefono");
  }

  private static String requireText(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new DomainException(field + " es requerido");
    }
    return value.trim();
  }

  private static String normalizeOptional(String value) {
    return value == null || value.isBlank() ? null : value.trim();
  }
}

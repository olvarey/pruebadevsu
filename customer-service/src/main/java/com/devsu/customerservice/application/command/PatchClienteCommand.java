package com.devsu.customerservice.application.command;

/** Immutable application input containing optional fields for a partial update. */
public record PatchClienteCommand(
    String nombre,
    String genero,
    Integer edad,
    String identificacion,
    String direccion,
    String telefono,
    String contrasena,
    Boolean estado) {}

package com.devsu.customerservice.application.command;

/** Immutable application input containing complete data for replacing a customer. */
public record ReplaceClienteCommand(
    String clienteId,
    String nombre,
    String genero,
    Integer edad,
    String identificacion,
    String direccion,
    String telefono,
    String contrasena,
    boolean estado) {}

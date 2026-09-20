package com.devsu.customerservice.application.command;

/** Immutable application input required to create a customer. */
public record CreateClienteCommand(
    String clienteId,
    String nombre,
    String genero,
    Integer edad,
    String identificacion,
    String direccion,
    String telefono,
    String contrasena,
    boolean estado) {}

package com.devsu.customerservice.infrastructure.adapter.in.web.dto;

/** Customer representation returned by the API. */
public record ClienteResponse(
    String clienteId,
    String nombre,
    String genero,
    Integer edad,
    String identificacion,
    String direccion,
    String telefono,
    boolean estado) {}

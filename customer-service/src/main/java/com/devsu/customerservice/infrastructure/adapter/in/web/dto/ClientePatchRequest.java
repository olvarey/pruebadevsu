package com.devsu.customerservice.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.PositiveOrZero;

/** Request body used to partially update a customer. */
public record ClientePatchRequest(
    String nombre,
    String genero,
    @PositiveOrZero Integer edad,
    String identificacion,
    String direccion,
    String telefono,
    String contrasena,
    Boolean estado) {}

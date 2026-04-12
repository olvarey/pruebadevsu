package com.devsu.customerservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/** Request body used to create or fully replace a customer. */
public record ClienteRequest(
    @NotBlank String clienteId,
    @NotBlank String nombre,
    String genero,
    @PositiveOrZero Integer edad,
    @NotBlank String identificacion,
    @NotBlank String direccion,
    @NotBlank String telefono,
    @NotBlank String contrasena,
    @NotNull Boolean estado) {}

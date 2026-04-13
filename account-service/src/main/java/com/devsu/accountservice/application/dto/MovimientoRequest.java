package com.devsu.accountservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Request body used to create a movement. */
public record MovimientoRequest(
    @NotBlank(message = "numeroCuenta es requerido") String numeroCuenta,
    LocalDateTime fecha,
    @NotNull(message = "valor es requerido") BigDecimal valor) {}

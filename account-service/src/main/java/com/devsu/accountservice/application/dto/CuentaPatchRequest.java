package com.devsu.accountservice.application.dto;

import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/** Request body used to partially update an account. */
public record CuentaPatchRequest(
    String tipoCuenta,
    @PositiveOrZero(message = "saldoInicial no puede ser negativo") BigDecimal saldoInicial,
    Boolean estado,
    String clienteId) {}

package com.devsu.accountservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/** Request body used to create or replace an account. */
public record CuentaRequest(
    @NotBlank(message = "numeroCuenta es requerido") String numeroCuenta,
    @NotBlank(message = "tipoCuenta es requerido") String tipoCuenta,
    @NotNull(message = "saldoInicial es requerido")
        @PositiveOrZero(message = "saldoInicial no puede ser negativo")
        BigDecimal saldoInicial,
    @NotNull(message = "estado es requerido") Boolean estado,
    @NotBlank(message = "clienteId es requerido") String clienteId) {}

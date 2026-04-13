package com.devsu.accountservice.application.dto;

import java.math.BigDecimal;

/** Account data returned by the account REST API. */
public record CuentaResponse(
    String numeroCuenta,
    String tipoCuenta,
    BigDecimal saldoInicial,
    BigDecimal saldoDisponible,
    boolean estado,
    String clienteId) {}

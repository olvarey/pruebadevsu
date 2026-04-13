package com.devsu.accountservice.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Account statement row returned by the report API. */
public record EstadoCuentaResponse(
    LocalDateTime fecha,
    String cliente,
    String numeroCuenta,
    String tipo,
    BigDecimal saldoInicial,
    boolean estado,
    BigDecimal movimiento,
    BigDecimal saldoDisponible) {}

package com.devsu.accountservice.application.result;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Framework-free account statement row returned by the application core. */
public record EstadoCuentaResult(
    LocalDateTime fecha,
    String cliente,
    String numeroCuenta,
    String tipo,
    BigDecimal saldoInicial,
    boolean estado,
    BigDecimal movimiento,
    BigDecimal saldoDisponible) {}

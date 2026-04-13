package com.devsu.accountservice.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Movement data returned by the account REST API. */
public record MovimientoResponse(
    String movimientoId,
    String numeroCuenta,
    LocalDateTime fecha,
    String tipoMovimiento,
    BigDecimal valor,
    BigDecimal saldo) {}

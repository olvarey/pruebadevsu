package com.devsu.accountservice.application.command;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Application input required to create an account movement. */
public record CreateMovimientoCommand(
    String numeroCuenta, LocalDateTime fecha, BigDecimal valor) {}

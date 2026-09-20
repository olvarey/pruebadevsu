package com.devsu.accountservice.application.command;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Complete application input used to replace movement data. */
public record ReplaceMovimientoCommand(
    String numeroCuenta, LocalDateTime fecha, BigDecimal valor) {}

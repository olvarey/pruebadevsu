package com.devsu.accountservice.application.command;

import java.math.BigDecimal;

/** Complete application input used to replace account data. */
public record ReplaceCuentaCommand(
    String numeroCuenta,
    String tipoCuenta,
    BigDecimal saldoInicial,
    boolean estado,
    String clienteId) {}

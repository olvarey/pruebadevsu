package com.devsu.accountservice.application.command;

import java.math.BigDecimal;

/** Application input required to create an account. */
public record CreateCuentaCommand(
    String numeroCuenta,
    String tipoCuenta,
    BigDecimal saldoInicial,
    boolean estado,
    String clienteId) {}

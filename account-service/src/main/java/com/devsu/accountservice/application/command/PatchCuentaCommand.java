package com.devsu.accountservice.application.command;

import java.math.BigDecimal;

/** Optional account fields used for a partial update. */
public record PatchCuentaCommand(
    String tipoCuenta,
    BigDecimal saldoInicial,
    Boolean estado,
    String clienteId) {}

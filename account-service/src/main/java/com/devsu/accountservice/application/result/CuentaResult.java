package com.devsu.accountservice.application.result;

import com.devsu.accountservice.domain.model.Cuenta;
import java.math.BigDecimal;

/** Framework-free account result returned by the application core. */
public record CuentaResult(
    String numeroCuenta,
    String tipoCuenta,
    BigDecimal saldoInicial,
    BigDecimal saldoDisponible,
    boolean estado,
    String clienteId) {

  /** Creates an application result from an account aggregate. */
  public static CuentaResult from(Cuenta cuenta) {
    return new CuentaResult(
        cuenta.getNumeroCuenta(), cuenta.getTipoCuenta(), cuenta.getSaldoInicial(),
        cuenta.getSaldoDisponible(), cuenta.isEstado(), cuenta.getClienteId());
  }
}

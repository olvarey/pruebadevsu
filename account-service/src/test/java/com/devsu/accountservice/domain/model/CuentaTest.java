package com.devsu.accountservice.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.devsu.accountservice.domain.exception.SaldoNoDisponibleException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for account balance domain rules.
 */
class CuentaTest {

  private static final BigDecimal INSUFFICIENT_WITHDRAWAL = new BigDecimal("-150.00");

  @Test
  void aplicarMovimientoUpdatesAvailableBalance() {
    Cuenta cuenta = cuentaConSaldo("100.00");

    Cuenta actualizada = cuenta.aplicarMovimiento(new BigDecimal("-25.50"));

    assertThat(actualizada.getSaldoDisponible()).isEqualByComparingTo("74.50");
  }

  @Test
  void aplicarMovimientoRejectsInsufficientFunds() {
    Cuenta cuenta = cuentaConSaldo("100.00");

    assertThatExceptionOfType(SaldoNoDisponibleException.class).isThrownBy(
        () -> cuenta.aplicarMovimiento(INSUFFICIENT_WITHDRAWAL)).withMessage("Saldo no disponible");
  }

  private Cuenta cuentaConSaldo(String saldo) {
    return new Cuenta("478758",
        new DatosCuenta("Ahorro", new BigDecimal("100.00"), new BigDecimal(saldo), true,
            "CLI-001"));
  }
}

package com.devsu.accountservice.domain.model;

import com.devsu.accountservice.domain.exception.DomainException;
import com.devsu.accountservice.domain.exception.SaldoNoDisponibleException;
import java.math.BigDecimal;

/** Account aggregate root that owns balance calculations. */
public class Cuenta {

  private final String numeroCuenta;
  private final DatosCuenta datosCuenta;

  /** Creates an account and validates its business identifier. */
  public Cuenta(String numeroCuenta, DatosCuenta datosCuenta) {
    this.numeroCuenta = requireText(numeroCuenta, "numeroCuenta");
    this.datosCuenta = datosCuenta;
  }

  /** Returns a copy with updated descriptive data while preserving the current balance. */
  public Cuenta actualizarDatos(
      String tipoCuenta, BigDecimal saldoInicial, boolean estado, String clienteId) {
    return new Cuenta(
        numeroCuenta,
        new DatosCuenta(tipoCuenta, saldoInicial, getSaldoDisponible(), estado, clienteId));
  }

  /** Returns a copy after applying a movement value to the available balance. */
  public Cuenta aplicarMovimiento(BigDecimal valor) {
    BigDecimal nuevoSaldo = getSaldoDisponible().add(requireMovementValue(valor));
    if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
      throw new SaldoNoDisponibleException();
    }
    return withSaldoDisponible(nuevoSaldo);
  }

  /** Returns a copy after applying the difference between two movement values. */
  public Cuenta ajustarMovimiento(BigDecimal valorAnterior, BigDecimal valorNuevo) {
    BigDecimal valorActual = requireMovementValue(valorAnterior);
    BigDecimal valorActualizado = requireMovementValue(valorNuevo);
    if (valorActualizado.compareTo(valorActual) == 0) {
      return this;
    }
    BigDecimal delta = valorActualizado.subtract(valorActual);
    return aplicarMovimiento(delta);
  }

  /** Returns the account number used by account endpoints. */
  public String getNumeroCuenta() {
    return numeroCuenta;
  }

  /** Returns the account type. */
  public String getTipoCuenta() {
    return datosCuenta.tipoCuenta();
  }

  /** Returns the balance used when the account was opened. */
  public BigDecimal getSaldoInicial() {
    return datosCuenta.saldoInicial();
  }

  /** Returns the current available balance. */
  public BigDecimal getSaldoDisponible() {
    return datosCuenta.saldoDisponible();
  }

  /** Returns whether the account is active. */
  public boolean isEstado() {
    return datosCuenta.estado();
  }

  /** Returns the owning customer business identifier. */
  public String getClienteId() {
    return datosCuenta.clienteId();
  }

  private Cuenta withSaldoDisponible(BigDecimal saldoDisponible) {
    return new Cuenta(
        numeroCuenta,
        new DatosCuenta(
            getTipoCuenta(), getSaldoInicial(), saldoDisponible, isEstado(),
            getClienteId()));
  }

  private static BigDecimal requireMovementValue(BigDecimal value) {
    if (value == null) {
      throw new DomainException("valor es requerido");
    }
    if (value.compareTo(BigDecimal.ZERO) == 0) {
      throw new DomainException("valor no puede ser cero");
    }
    return value;
  }

  private static String requireText(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new DomainException(field + " es requerido");
    }
    return value.trim();
  }
}

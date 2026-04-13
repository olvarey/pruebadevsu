package com.devsu.accountservice.domain.model;

import com.devsu.accountservice.domain.exception.DomainException;
import java.math.BigDecimal;

/** Account data grouped to keep account construction readable. */
public record DatosCuenta(
    String tipoCuenta,
    BigDecimal saldoInicial,
    BigDecimal saldoDisponible,
    boolean estado,
    String clienteId) {

  /** Creates account data and validates the fields required by the domain. */
  public DatosCuenta {
    tipoCuenta = requireText(tipoCuenta, "tipoCuenta");
    requireNonNegative(saldoInicial, "saldoInicial");
    requireNonNegative(saldoDisponible, "saldoDisponible");
    clienteId = requireText(clienteId, "clienteId");
  }

  private static String requireText(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new DomainException(field + " es requerido");
    }
    return value.trim();
  }

  private static void requireNonNegative(BigDecimal value, String field) {
    if (value == null) {
      throw new DomainException(field + " es requerido");
    }
    if (value.compareTo(BigDecimal.ZERO) < 0) {
      throw new DomainException(field + " no puede ser negativo");
    }
  }
}

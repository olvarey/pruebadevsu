package com.devsu.accountservice.domain.model;

import com.devsu.accountservice.domain.exception.DomainException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Movement data grouped to keep movement construction readable. */
public record DatosMovimiento(
    String numeroCuenta,
    LocalDateTime fecha,
    String tipoMovimiento,
    BigDecimal valor,
    BigDecimal saldo) {

  /** Creates movement data and validates the fields required by the domain. */
  public DatosMovimiento {
    numeroCuenta = requireText(numeroCuenta, "numeroCuenta");
    fecha = fecha == null ? LocalDateTime.now() : fecha;
    tipoMovimiento = requireText(tipoMovimiento, "tipoMovimiento");
    requireMovementValue(valor);
    requireNonNegative(saldo, "saldo");
  }

  /** Returns the movement type that corresponds to the value sign. */
  public static String tipoPara(BigDecimal valor) {
    return requireMovementValue(valor).compareTo(BigDecimal.ZERO) < 0 ? "Retiro" : "Deposito";
  }

  private static String requireText(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new DomainException(field + " es requerido");
    }
    return value.trim();
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

  private static void requireNonNegative(BigDecimal value, String field) {
    if (value == null) {
      throw new DomainException(field + " es requerido");
    }
    if (value.compareTo(BigDecimal.ZERO) < 0) {
      throw new DomainException(field + " no puede ser negativo");
    }
  }
}

package com.devsu.accountservice.domain.model;

import com.devsu.accountservice.domain.exception.DomainException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Account movement registered after a balance change. */
public class Movimiento {

  private final String movimientoId;
  private final DatosMovimiento datosMovimiento;

  /** Creates a movement and validates its identifier. */
  public Movimiento(String movimientoId, DatosMovimiento datosMovimiento) {
    this.movimientoId = requireText(movimientoId, "movimientoId");
    this.datosMovimiento = datosMovimiento;
  }

  /** Returns a copy with updated date, value, type, and resulting balance. */
  public Movimiento actualizar(LocalDateTime fecha, BigDecimal valor, BigDecimal saldo) {
    return new Movimiento(
        movimientoId,
        new DatosMovimiento(
            getNumeroCuenta(), fecha, DatosMovimiento.tipoPara(valor), valor, saldo));
  }

  /** Returns the movement identifier. */
  public String getMovimientoId() {
    return movimientoId;
  }

  /** Returns the account number affected by this movement. */
  public String getNumeroCuenta() {
    return datosMovimiento.numeroCuenta();
  }

  /** Returns when this movement was registered. */
  public LocalDateTime getFecha() {
    return datosMovimiento.fecha();
  }

  /** Returns the movement type derived from the movement value. */
  public String getTipoMovimiento() {
    return datosMovimiento.tipoMovimiento();
  }

  /** Returns the movement value. Negative values represent withdrawals. */
  public BigDecimal getValor() {
    return datosMovimiento.valor();
  }

  /** Returns the account balance after this movement. */
  public BigDecimal getSaldo() {
    return datosMovimiento.saldo();
  }

  private static String requireText(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new DomainException(field + " es requerido");
    }
    return value.trim();
  }
}

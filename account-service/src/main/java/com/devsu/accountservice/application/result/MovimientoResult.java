package com.devsu.accountservice.application.result;

import com.devsu.accountservice.domain.model.Movimiento;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Framework-free movement result returned by the application core. */
public record MovimientoResult(
    String movimientoId,
    String numeroCuenta,
    LocalDateTime fecha,
    String tipoMovimiento,
    BigDecimal valor,
    BigDecimal saldo) {

  /** Creates an application result from a movement aggregate. */
  public static MovimientoResult from(Movimiento movimiento) {
    return new MovimientoResult(
        movimiento.getMovimientoId(), movimiento.getNumeroCuenta(), movimiento.getFecha(),
        movimiento.getTipoMovimiento(), movimiento.getValor(), movimiento.getSaldo());
  }
}

package com.devsu.accountservice.application.port.out;

import com.devsu.accountservice.domain.model.Movimiento;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Outbound persistence port for movement aggregates. */
public interface MovimientoRepository {

  /** Finds a movement by identifier. */
  Optional<Movimiento> findByMovimientoId(String movimientoId);

  /** Lists all movements. */
  List<Movimiento> findAll();

  /** Finds movements for accounts inside an inclusive date range. */
  List<Movimiento> findByNumeroCuentaInAndFechaBetween(
      List<String> numerosCuenta, LocalDateTime start, LocalDateTime end);

  /** Saves a movement aggregate. */
  Movimiento save(Movimiento movimiento);
}

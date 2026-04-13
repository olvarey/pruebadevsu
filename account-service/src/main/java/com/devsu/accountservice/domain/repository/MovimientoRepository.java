package com.devsu.accountservice.domain.repository;

import com.devsu.accountservice.domain.model.Movimiento;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Persistence port for account movements. */
public interface MovimientoRepository {

  /** Finds a movement by its identifier. */
  Optional<Movimiento> findByMovimientoId(String movimientoId);

  /** Lists every registered movement. */
  List<Movimiento> findAll();

  /** Lists movements for the provided accounts inside an inclusive date range. */
  List<Movimiento> findByNumeroCuentaInAndFechaBetween(
      List<String> numerosCuenta, LocalDateTime start, LocalDateTime end);

  /** Saves a movement. */
  Movimiento save(Movimiento movimiento);
}

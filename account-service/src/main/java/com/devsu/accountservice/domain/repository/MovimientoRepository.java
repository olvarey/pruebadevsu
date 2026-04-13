package com.devsu.accountservice.domain.repository;

import com.devsu.accountservice.domain.model.Movimiento;
import java.util.List;
import java.util.Optional;

/** Persistence port for account movements. */
public interface MovimientoRepository {

  /** Finds a movement by its identifier. */
  Optional<Movimiento> findByMovimientoId(String movimientoId);

  /** Lists every registered movement. */
  List<Movimiento> findAll();

  /** Saves a movement. */
  Movimiento save(Movimiento movimiento);
}

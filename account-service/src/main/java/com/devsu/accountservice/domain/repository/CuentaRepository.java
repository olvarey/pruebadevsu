package com.devsu.accountservice.domain.repository;

import com.devsu.accountservice.domain.model.Cuenta;
import java.util.List;
import java.util.Optional;

/** Persistence port for account aggregates. */
public interface CuentaRepository {

  /** Returns whether an account exists with the provided account number. */
  boolean existsByNumeroCuenta(String numeroCuenta);

  /** Finds an account by its account number. */
  Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);

  /** Lists every account. */
  List<Cuenta> findAll();

  /** Lists every account owned by a customer. */
  List<Cuenta> findByClienteId(String clienteId);

  /** Saves an account aggregate. */
  Cuenta save(Cuenta cuenta);
}

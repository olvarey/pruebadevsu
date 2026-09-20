package com.devsu.accountservice.application.port.out;

import com.devsu.accountservice.domain.model.Cuenta;
import java.util.List;
import java.util.Optional;

/** Outbound persistence port for account aggregates. */
public interface CuentaRepository {

  /** Checks whether an account number already exists. */
  boolean existsByNumeroCuenta(String numeroCuenta);

  /** Finds an account by account number. */
  Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);

  /** Lists all accounts. */
  List<Cuenta> findAll();

  /** Lists accounts owned by a customer. */
  List<Cuenta> findByClienteId(String clienteId);

  /** Saves an account aggregate. */
  Cuenta save(Cuenta cuenta);
}

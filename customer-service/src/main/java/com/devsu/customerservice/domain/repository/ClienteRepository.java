package com.devsu.customerservice.domain.repository;

import com.devsu.customerservice.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

/** Persistence port for customer aggregate operations. */
public interface ClienteRepository {

  /** Saves the customer aggregate and returns the persisted state. */
  Cliente save(Cliente cliente);

  /** Finds a customer by its business identifier. */
  Optional<Cliente> findByClienteId(String clienteId);

  /** Finds a customer by its unique personal identification. */
  Optional<Cliente> findByIdentificacion(String identificacion);

  /** Returns every customer registered in the service. */
  List<Cliente> findAll();

  /** Returns whether a customer business identifier already exists. */
  boolean existsByClienteId(String clienteId);
}

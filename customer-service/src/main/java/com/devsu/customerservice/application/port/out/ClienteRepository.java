package com.devsu.customerservice.application.port.out;

import com.devsu.customerservice.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

/** Outbound persistence port for the customer aggregate. */
public interface ClienteRepository {

  /** Saves a customer aggregate and returns its persisted state. */
  Cliente save(Cliente cliente);

  /** Finds a customer by its business identifier. */
  Optional<Cliente> findByClienteId(String clienteId);

  /** Finds a customer by its unique personal identification. */
  Optional<Cliente> findByIdentificacion(String identificacion);

  /** Returns all persisted customers. */
  List<Cliente> findAll();

  /** Checks whether a customer business identifier already exists. */
  boolean existsByClienteId(String clienteId);
}

package com.devsu.customerservice.domain.repository;

import com.devsu.customerservice.domain.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository {

    Cliente save(Cliente cliente);

    Optional<Cliente> findByClienteId(String clienteId);

    Optional<Cliente> findByIdentificacion(String identificacion);

    List<Cliente> findAll();

    boolean existsByClienteId(String clienteId);
}

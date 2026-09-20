package com.devsu.customerservice.infrastructure.adapter.out.persistence.adapter;

import com.devsu.customerservice.domain.model.Cliente;
import com.devsu.customerservice.application.port.out.ClienteRepository;
import com.devsu.customerservice.infrastructure.adapter.out.persistence.mapper.ClienteEntityMapper;
import com.devsu.customerservice.infrastructure.adapter.out.persistence.repository.SpringDataClienteRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** JPA adapter that implements the customer repository port. */
@RequiredArgsConstructor
@Repository
public class ClientePersistenceAdapter implements ClienteRepository {

  private final SpringDataClienteRepository repository;
  private final ClienteEntityMapper clienteEntityMapper;

  /** {@inheritDoc} */
  @Override
  public Cliente save(Cliente cliente) {
    return clienteEntityMapper.toDomain(repository.save(clienteEntityMapper.toEntity(cliente)));
  }

  /** {@inheritDoc} */
  @Override
  public Optional<Cliente> findByClienteId(String clienteId) {
    return repository.findById(clienteId).map(clienteEntityMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Override
  public Optional<Cliente> findByIdentificacion(String identificacion) {
    return repository.findByIdentificacion(identificacion).map(clienteEntityMapper::toDomain);
  }

  /** {@inheritDoc} */
  @Override
  public List<Cliente> findAll() {
    return repository.findAll().stream().map(clienteEntityMapper::toDomain).toList();
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsByClienteId(String clienteId) {
    return repository.existsById(clienteId);
  }
}

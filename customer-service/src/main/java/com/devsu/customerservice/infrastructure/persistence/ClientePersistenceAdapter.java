package com.devsu.customerservice.infrastructure.persistence;

import com.devsu.customerservice.domain.model.Cliente;
import com.devsu.customerservice.domain.repository.ClienteRepository;
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

  @Override
  public Cliente save(Cliente cliente) {
    return clienteEntityMapper.toDomain(repository.save(clienteEntityMapper.toEntity(cliente)));
  }

  @Override
  public Optional<Cliente> findByClienteId(String clienteId) {
    return repository.findById(clienteId).map(clienteEntityMapper::toDomain);
  }

  @Override
  public Optional<Cliente> findByIdentificacion(String identificacion) {
    return repository.findByIdentificacion(identificacion).map(clienteEntityMapper::toDomain);
  }

  @Override
  public List<Cliente> findAll() {
    return repository.findAll().stream().map(clienteEntityMapper::toDomain).toList();
  }

  @Override
  public boolean existsByClienteId(String clienteId) {
    return repository.existsById(clienteId);
  }
}

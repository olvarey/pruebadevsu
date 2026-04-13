package com.devsu.accountservice.infrastructure.persistence.adapter;

import com.devsu.accountservice.domain.model.Cuenta;
import com.devsu.accountservice.domain.repository.CuentaRepository;
import com.devsu.accountservice.infrastructure.persistence.entity.CuentaEntity;
import com.devsu.accountservice.infrastructure.persistence.mapper.CuentaEntityMapper;
import com.devsu.accountservice.infrastructure.persistence.repository.SpringDataCuentaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Account persistence adapter backed by Spring Data JPA. */
@RequiredArgsConstructor
@Repository
public class CuentaPersistenceAdapter implements CuentaRepository {

  private final SpringDataCuentaRepository springDataCuentaRepository;
  private final CuentaEntityMapper cuentaEntityMapper;

  @Override
  public boolean existsByNumeroCuenta(String numeroCuenta) {
    return springDataCuentaRepository.existsById(numeroCuenta);
  }

  @Override
  public Optional<Cuenta> findByNumeroCuenta(String numeroCuenta) {
    return springDataCuentaRepository.findById(numeroCuenta).map(cuentaEntityMapper::toDomain);
  }

  @Override
  public List<Cuenta> findAll() {
    return springDataCuentaRepository.findAll().stream()
        .map(cuentaEntityMapper::toDomain)
        .toList();
  }

  @Override
  public List<Cuenta> findByClienteId(String clienteId) {
    return springDataCuentaRepository.findByClienteId(clienteId).stream()
        .map(cuentaEntityMapper::toDomain)
        .toList();
  }

  @Override
  public Cuenta save(Cuenta cuenta) {
    CuentaEntity entity = cuentaEntityMapper.toEntity(cuenta);
    return cuentaEntityMapper.toDomain(springDataCuentaRepository.save(entity));
  }
}

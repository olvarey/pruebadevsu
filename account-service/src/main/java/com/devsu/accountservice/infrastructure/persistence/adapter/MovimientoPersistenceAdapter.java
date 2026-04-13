package com.devsu.accountservice.infrastructure.persistence.adapter;

import com.devsu.accountservice.domain.model.Movimiento;
import com.devsu.accountservice.domain.repository.MovimientoRepository;
import com.devsu.accountservice.infrastructure.persistence.entity.MovimientoEntity;
import com.devsu.accountservice.infrastructure.persistence.mapper.MovimientoEntityMapper;
import com.devsu.accountservice.infrastructure.persistence.repository.SpringDataMovimientoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Movement persistence adapter backed by Spring Data JPA. */
@RequiredArgsConstructor
@Repository
public class MovimientoPersistenceAdapter implements MovimientoRepository {

  private final SpringDataMovimientoRepository springDataMovimientoRepository;
  private final MovimientoEntityMapper movimientoEntityMapper;

  @Override
  public Optional<Movimiento> findByMovimientoId(String movimientoId) {
    return springDataMovimientoRepository.findById(movimientoId)
        .map(movimientoEntityMapper::toDomain);
  }

  @Override
  public List<Movimiento> findAll() {
    return springDataMovimientoRepository.findAll().stream()
        .map(movimientoEntityMapper::toDomain)
        .toList();
  }

  @Override
  public List<Movimiento> findByNumeroCuentaInAndFechaBetween(
      List<String> numerosCuenta, LocalDateTime start, LocalDateTime end) {
    return springDataMovimientoRepository
        .findByNumeroCuentaInAndFechaBetweenOrderByFechaAsc(numerosCuenta, start, end)
        .stream()
        .map(movimientoEntityMapper::toDomain)
        .toList();
  }

  @Override
  public Movimiento save(Movimiento movimiento) {
    MovimientoEntity entity = movimientoEntityMapper.toEntity(movimiento);
    return movimientoEntityMapper.toDomain(springDataMovimientoRepository.save(entity));
  }
}

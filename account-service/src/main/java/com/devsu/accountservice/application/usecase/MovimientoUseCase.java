package com.devsu.accountservice.application.usecase;

import com.devsu.accountservice.application.dto.MovimientoPatchRequest;
import com.devsu.accountservice.application.dto.MovimientoRequest;
import com.devsu.accountservice.application.dto.MovimientoResponse;
import com.devsu.accountservice.application.mapper.MovimientoMapper;
import com.devsu.accountservice.domain.exception.CuentaNoEncontradaException;
import com.devsu.accountservice.domain.exception.MovimientoNoEncontradoException;
import com.devsu.accountservice.domain.model.Cuenta;
import com.devsu.accountservice.domain.model.Movimiento;
import com.devsu.accountservice.domain.repository.CuentaRepository;
import com.devsu.accountservice.domain.repository.MovimientoRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Application service that coordinates movement use cases. */
@RequiredArgsConstructor
@Service
public class MovimientoUseCase {

  private final CuentaRepository cuentaRepository;
  private final MovimientoRepository movimientoRepository;
  private final MovimientoMapper movimientoMapper;

  /** Creates a movement, updates the account balance, and stores the resulting transaction. */
  @Transactional
  public MovimientoResponse create(MovimientoRequest request) {
    Cuenta cuenta = findCuenta(request.numeroCuenta());
    Cuenta cuentaActualizada = cuenta.aplicarMovimiento(request.valor());
    cuentaRepository.save(cuentaActualizada);
    Movimiento movimiento =
        movimientoMapper.toDomain(request, cuentaActualizada.getSaldoDisponible());
    return movimientoMapper.toResponse(movimientoRepository.save(movimiento));
  }

  /** Lists all movements. */
  @Transactional(readOnly = true)
  public List<MovimientoResponse> list() {
    return movimientoRepository.findAll().stream().map(movimientoMapper::toResponse).toList();
  }

  /** Returns one movement by identifier. */
  @Transactional(readOnly = true)
  public MovimientoResponse get(String movimientoId) {
    return movimientoMapper.toResponse(findMovimiento(movimientoId));
  }

  /** Replaces movement editable data and adjusts the current account balance by the value delta. */
  @Transactional
  public MovimientoResponse replace(String movimientoId, MovimientoRequest request) {
    Movimiento current = findMovimiento(movimientoId);
    ensureSameAccount(current, request.numeroCuenta());
    updateAccountBalance(current, request.valor());
    BigDecimal saldoMovimiento = movementBalanceAfterDelta(current, request.valor());
    Movimiento updated = current.actualizar(request.fecha(), request.valor(), saldoMovimiento);
    return movimientoMapper.toResponse(movimientoRepository.save(updated));
  }

  /** Applies a partial movement update and adjusts the current account balance when needed. */
  @Transactional
  public MovimientoResponse patch(String movimientoId, MovimientoPatchRequest request) {
    Movimiento current = findMovimiento(movimientoId);
    BigDecimal nuevoValor = request.valor() == null ? current.getValor() : request.valor();
    updateAccountBalance(current, nuevoValor);
    BigDecimal saldoMovimiento = movementBalanceAfterDelta(current, nuevoValor);
    Movimiento updated = movimientoMapper.mergePatch(current, request, saldoMovimiento);
    return movimientoMapper.toResponse(movimientoRepository.save(updated));
  }

  private void updateAccountBalance(Movimiento current, BigDecimal nuevoValor) {
    Cuenta cuenta = findCuenta(current.getNumeroCuenta());
    Cuenta cuentaActualizada = cuenta.ajustarMovimiento(current.getValor(), nuevoValor);
    cuentaRepository.save(cuentaActualizada);
  }

  private BigDecimal movementBalanceAfterDelta(Movimiento current, BigDecimal nuevoValor) {
    BigDecimal delta = nuevoValor.subtract(current.getValor());
    return current.getSaldo().add(delta);
  }

  private void ensureSameAccount(Movimiento current, String numeroCuenta) {
    if (!current.getNumeroCuenta().equals(numeroCuenta)) {
      throw new CuentaNoEncontradaException(numeroCuenta);
    }
  }

  private Cuenta findCuenta(String numeroCuenta) {
    return cuentaRepository.findByNumeroCuenta(numeroCuenta)
        .orElseThrow(() -> new CuentaNoEncontradaException(numeroCuenta));
  }

  private Movimiento findMovimiento(String movimientoId) {
    return movimientoRepository.findByMovimientoId(movimientoId)
        .orElseThrow(() -> new MovimientoNoEncontradoException(movimientoId));
  }
}

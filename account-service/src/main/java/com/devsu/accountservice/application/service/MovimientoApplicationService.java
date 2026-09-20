package com.devsu.accountservice.application.service;

import com.devsu.accountservice.application.command.CreateMovimientoCommand;
import com.devsu.accountservice.application.command.PatchMovimientoCommand;
import com.devsu.accountservice.application.command.ReplaceMovimientoCommand;
import com.devsu.accountservice.application.port.in.MovimientoInputPort;
import com.devsu.accountservice.application.port.out.CuentaRepository;
import com.devsu.accountservice.application.port.out.MovimientoRepository;
import com.devsu.accountservice.application.result.MovimientoResult;
import com.devsu.accountservice.domain.exception.CuentaNoEncontradaException;
import com.devsu.accountservice.domain.exception.MovimientoNoEncontradoException;
import com.devsu.accountservice.domain.model.Cuenta;
import com.devsu.accountservice.domain.model.DatosMovimiento;
import com.devsu.accountservice.domain.model.Movimiento;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** Framework-free application service for movement use cases. */
public class MovimientoApplicationService implements MovimientoInputPort {

  private final CuentaRepository cuentaRepository;
  private final MovimientoRepository movimientoRepository;

  /** Creates a movement service with its account and movement ports. */
  public MovimientoApplicationService(
      CuentaRepository cuentaRepository, MovimientoRepository movimientoRepository) {
    this.cuentaRepository = cuentaRepository;
    this.movimientoRepository = movimientoRepository;
  }

  /** {@inheritDoc} */
  @Override
  public MovimientoResult create(CreateMovimientoCommand command) {
    Cuenta cuentaActualizada = findCuenta(command.numeroCuenta()).aplicarMovimiento(command.valor());
    cuentaRepository.save(cuentaActualizada);
    Movimiento movimiento = new Movimiento(
        UUID.randomUUID().toString(),
        new DatosMovimiento(command.numeroCuenta(), command.fecha(),
            DatosMovimiento.tipoPara(command.valor()), command.valor(),
            cuentaActualizada.getSaldoDisponible()));
    return MovimientoResult.from(movimientoRepository.save(movimiento));
  }

  /** {@inheritDoc} */
  @Override
  public MovimientoResult get(String movimientoId) {
    return MovimientoResult.from(findMovimiento(movimientoId));
  }

  /** {@inheritDoc} */
  @Override
  public List<MovimientoResult> list() {
    return movimientoRepository.findAll().stream().map(MovimientoResult::from).toList();
  }

  /** {@inheritDoc} */
  @Override
  public MovimientoResult replace(String movimientoId, ReplaceMovimientoCommand command) {
    Movimiento current = findMovimiento(movimientoId);
    ensureSameAccount(current, command.numeroCuenta());
    updateAccountBalance(current, command.valor());
    Movimiento updated = current.actualizar(
        command.fecha(), command.valor(), movementBalanceAfterDelta(current, command.valor()));
    return MovimientoResult.from(movimientoRepository.save(updated));
  }

  /** {@inheritDoc} */
  @Override
  public MovimientoResult patch(String movimientoId, PatchMovimientoCommand command) {
    Movimiento current = findMovimiento(movimientoId);
    BigDecimal nuevoValor = command.valor() == null ? current.getValor() : command.valor();
    updateAccountBalance(current, nuevoValor);
    Movimiento updated = current.actualizar(
        command.fecha() == null ? current.getFecha() : command.fecha(), nuevoValor,
        movementBalanceAfterDelta(current, nuevoValor));
    return MovimientoResult.from(movimientoRepository.save(updated));
  }

  private void updateAccountBalance(Movimiento current, BigDecimal nuevoValor) {
    Cuenta cuentaActualizada = findCuenta(current.getNumeroCuenta())
        .ajustarMovimiento(current.getValor(), nuevoValor);
    cuentaRepository.save(cuentaActualizada);
  }

  private BigDecimal movementBalanceAfterDelta(Movimiento current, BigDecimal nuevoValor) {
    return current.getSaldo().add(nuevoValor.subtract(current.getValor()));
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

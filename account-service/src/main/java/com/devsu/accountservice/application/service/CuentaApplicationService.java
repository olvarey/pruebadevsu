package com.devsu.accountservice.application.service;

import com.devsu.accountservice.application.command.CreateCuentaCommand;
import com.devsu.accountservice.application.command.PatchCuentaCommand;
import com.devsu.accountservice.application.command.ReplaceCuentaCommand;
import com.devsu.accountservice.application.port.in.CuentaInputPort;
import com.devsu.accountservice.application.port.out.CuentaRepository;
import com.devsu.accountservice.application.result.CuentaResult;
import com.devsu.accountservice.domain.exception.CuentaDuplicadaException;
import com.devsu.accountservice.domain.exception.CuentaNoEncontradaException;
import com.devsu.accountservice.domain.model.Cuenta;
import com.devsu.accountservice.domain.model.DatosCuenta;
import java.util.List;

/** Framework-free application service for account use cases. */
public class CuentaApplicationService implements CuentaInputPort {

  private final CuentaRepository cuentaRepository;

  /** Creates an account service with its persistence port. */
  public CuentaApplicationService(CuentaRepository cuentaRepository) {
    this.cuentaRepository = cuentaRepository;
  }

  /** {@inheritDoc} */
  @Override
  public CuentaResult create(CreateCuentaCommand command) {
    if (cuentaRepository.existsByNumeroCuenta(command.numeroCuenta())) {
      throw new CuentaDuplicadaException(command.numeroCuenta());
    }
    return CuentaResult.from(cuentaRepository.save(toDomain(command)));
  }

  /** {@inheritDoc} */
  @Override
  public CuentaResult get(String numeroCuenta) {
    return CuentaResult.from(findCuenta(numeroCuenta));
  }

  /** {@inheritDoc} */
  @Override
  public List<CuentaResult> list() {
    return cuentaRepository.findAll().stream().map(CuentaResult::from).toList();
  }

  /** {@inheritDoc} */
  @Override
  public CuentaResult replace(String numeroCuenta, ReplaceCuentaCommand command) {
    Cuenta current = findCuenta(numeroCuenta);
    Cuenta updated = current.actualizarDatos(
        command.tipoCuenta(), command.saldoInicial(), command.estado(), command.clienteId());
    return CuentaResult.from(cuentaRepository.save(updated));
  }

  /** {@inheritDoc} */
  @Override
  public CuentaResult patch(String numeroCuenta, PatchCuentaCommand command) {
    Cuenta current = findCuenta(numeroCuenta);
    Cuenta updated = current.actualizarDatos(
        valueOrCurrent(command.tipoCuenta(), current.getTipoCuenta()),
        valueOrCurrent(command.saldoInicial(), current.getSaldoInicial()),
        valueOrCurrent(command.estado(), current.isEstado()),
        valueOrCurrent(command.clienteId(), current.getClienteId()));
    return CuentaResult.from(cuentaRepository.save(updated));
  }

  private Cuenta toDomain(CreateCuentaCommand command) {
    return new Cuenta(
        command.numeroCuenta(),
        new DatosCuenta(command.tipoCuenta(), command.saldoInicial(), command.saldoInicial(),
            command.estado(), command.clienteId()));
  }

  private Cuenta findCuenta(String numeroCuenta) {
    return cuentaRepository.findByNumeroCuenta(numeroCuenta)
        .orElseThrow(() -> new CuentaNoEncontradaException(numeroCuenta));
  }

  private <T> T valueOrCurrent(T value, T current) {
    return value != null ? value : current;
  }
}

package com.devsu.accountservice.infrastructure.config;

import com.devsu.accountservice.application.command.CreateCuentaCommand;
import com.devsu.accountservice.application.command.PatchCuentaCommand;
import com.devsu.accountservice.application.command.ReplaceCuentaCommand;
import com.devsu.accountservice.application.port.in.CuentaInputPort;
import com.devsu.accountservice.application.result.CuentaResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

/** Infrastructure transaction boundary for account use cases. */
@RequiredArgsConstructor
public class TransactionalCuentaInputAdapter implements CuentaInputPort {

  private final CuentaInputPort delegate;

  /** {@inheritDoc} */
  @Override
  @Transactional
  public CuentaResult create(CreateCuentaCommand command) { return delegate.create(command); }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public CuentaResult get(String numeroCuenta) { return delegate.get(numeroCuenta); }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<CuentaResult> list() { return delegate.list(); }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public CuentaResult replace(String numeroCuenta, ReplaceCuentaCommand command) {
    return delegate.replace(numeroCuenta, command);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public CuentaResult patch(String numeroCuenta, PatchCuentaCommand command) {
    return delegate.patch(numeroCuenta, command);
  }
}

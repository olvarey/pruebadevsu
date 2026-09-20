package com.devsu.accountservice.infrastructure.config;

import com.devsu.accountservice.application.command.CreateMovimientoCommand;
import com.devsu.accountservice.application.command.PatchMovimientoCommand;
import com.devsu.accountservice.application.command.ReplaceMovimientoCommand;
import com.devsu.accountservice.application.port.in.MovimientoInputPort;
import com.devsu.accountservice.application.result.MovimientoResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

/** Infrastructure transaction boundary for movement use cases. */
@RequiredArgsConstructor
public class TransactionalMovimientoInputAdapter implements MovimientoInputPort {

  private final MovimientoInputPort delegate;

  /** {@inheritDoc} */
  @Override
  @Transactional
  public MovimientoResult create(CreateMovimientoCommand command) { return delegate.create(command); }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public MovimientoResult get(String movimientoId) { return delegate.get(movimientoId); }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<MovimientoResult> list() { return delegate.list(); }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public MovimientoResult replace(String movimientoId, ReplaceMovimientoCommand command) {
    return delegate.replace(movimientoId, command);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public MovimientoResult patch(String movimientoId, PatchMovimientoCommand command) {
    return delegate.patch(movimientoId, command);
  }
}

package com.devsu.customerservice.infrastructure.config;

import com.devsu.customerservice.application.command.CreateClienteCommand;
import com.devsu.customerservice.application.command.PatchClienteCommand;
import com.devsu.customerservice.application.command.ReplaceClienteCommand;
import com.devsu.customerservice.application.port.in.ClienteInputPort;
import com.devsu.customerservice.application.result.ClienteResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

/** Infrastructure boundary that owns Spring transaction metadata. */
@RequiredArgsConstructor
public class TransactionalClienteInputAdapter implements ClienteInputPort {

  private final ClienteInputPort delegate;

  /** {@inheritDoc} */
  @Override
  @Transactional
  public ClienteResult create(CreateClienteCommand command) {
    return delegate.create(command);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public ClienteResult get(String clienteId) {
    return delegate.get(clienteId);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<ClienteResult> list() {
    return delegate.list();
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public ClienteResult replace(String clienteId, ReplaceClienteCommand command) {
    return delegate.replace(clienteId, command);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public ClienteResult patch(String clienteId, PatchClienteCommand command) {
    return delegate.patch(clienteId, command);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public void delete(String clienteId) {
    delegate.delete(clienteId);
  }
}

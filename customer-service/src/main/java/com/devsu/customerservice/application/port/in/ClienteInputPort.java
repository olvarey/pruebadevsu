package com.devsu.customerservice.application.port.in;

import com.devsu.customerservice.application.command.CreateClienteCommand;
import com.devsu.customerservice.application.command.PatchClienteCommand;
import com.devsu.customerservice.application.command.ReplaceClienteCommand;
import com.devsu.customerservice.application.result.ClienteResult;
import java.util.List;

/** Inbound port for customer use cases. */
public interface ClienteInputPort {

  /** Creates a customer and returns its application representation. */
  ClienteResult create(CreateClienteCommand command);

  /** Finds a customer by its business identifier. */
  ClienteResult get(String clienteId);

  /** Lists all customers. */
  List<ClienteResult> list();

  /** Replaces a customer identified by the current business identifier. */
  ClienteResult replace(String clienteId, ReplaceClienteCommand command);

  /** Applies a partial update to a customer. */
  ClienteResult patch(String clienteId, PatchClienteCommand command);

  /** Deactivates a customer without deleting its persisted record. */
  void delete(String clienteId);
}

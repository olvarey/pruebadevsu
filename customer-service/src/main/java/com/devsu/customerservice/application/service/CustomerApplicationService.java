package com.devsu.customerservice.application.service;

import com.devsu.customerservice.application.command.CreateClienteCommand;
import com.devsu.customerservice.application.command.PatchClienteCommand;
import com.devsu.customerservice.application.command.ReplaceClienteCommand;
import com.devsu.customerservice.application.port.in.ClienteInputPort;
import com.devsu.customerservice.application.port.out.ClienteEvent;
import com.devsu.customerservice.application.port.out.ClienteEventPublisher;
import com.devsu.customerservice.application.port.out.ClienteEventType;
import com.devsu.customerservice.application.port.out.ClienteRepository;
import com.devsu.customerservice.application.result.ClienteResult;
import com.devsu.customerservice.domain.exception.ClienteDuplicadoException;
import com.devsu.customerservice.domain.exception.ClienteNoEncontradoException;
import com.devsu.customerservice.domain.model.Cliente;
import com.devsu.customerservice.domain.model.DatosPersona;
import java.util.List;

/** Framework-free application service for customer use cases. */
public class CustomerApplicationService implements ClienteInputPort {

  private final ClienteRepository clienteRepository;
  private final ClienteEventPublisher clienteEventPublisher;

  /** Creates an application service with its required outbound ports. */
  public CustomerApplicationService(
      ClienteRepository clienteRepository, ClienteEventPublisher clienteEventPublisher) {
    this.clienteRepository = clienteRepository;
    this.clienteEventPublisher = clienteEventPublisher;
  }

  /** {@inheritDoc} */
  @Override
  public ClienteResult create(CreateClienteCommand command) {
    ensureUniqueForCreate(command.clienteId(), command.identificacion());
    Cliente saved = clienteRepository.save(toDomain(command));
    publish(ClienteEventType.CLIENTE_CREADO, saved);
    return ClienteResult.from(saved);
  }

  /** {@inheritDoc} */
  @Override
  public ClienteResult get(String clienteId) {
    return ClienteResult.from(find(clienteId));
  }

  /** {@inheritDoc} */
  @Override
  public List<ClienteResult> list() {
    return clienteRepository.findAll().stream().map(ClienteResult::from).toList();
  }

  /** {@inheritDoc} */
  @Override
  public ClienteResult replace(String clienteId, ReplaceClienteCommand command) {
    find(clienteId);
    if (hasConflictingClienteId(clienteId, command.clienteId())) {
      throw new ClienteDuplicadoException("clienteId ya existe");
    }
    ensureIdentificationAvailable(command.identificacion(), clienteId);
    Cliente saved = clienteRepository.save(toDomain(command));
    publish(ClienteEventType.CLIENTE_ACTUALIZADO, saved);
    return ClienteResult.from(saved);
  }

  /** {@inheritDoc} */
  @Override
  public ClienteResult patch(String clienteId, PatchClienteCommand command) {
    Cliente current = find(clienteId);
    Cliente updated = mergePatch(current, command);
    ensureIdentificationAvailable(updated.getIdentificacion(), clienteId);
    Cliente saved = clienteRepository.save(updated);
    publish(ClienteEventType.CLIENTE_ACTUALIZADO, saved);
    return ClienteResult.from(saved);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(String clienteId) {
    Cliente saved = clienteRepository.save(find(clienteId).desactivar());
    publish(ClienteEventType.CLIENTE_DESACTIVADO, saved);
  }

  private Cliente find(String clienteId) {
    return clienteRepository.findByClienteId(clienteId)
        .orElseThrow(() -> new ClienteNoEncontradoException(clienteId));
  }

  private void ensureUniqueForCreate(String clienteId, String identificacion) {
    if (clienteRepository.existsByClienteId(clienteId)) {
      throw new ClienteDuplicadoException("clienteId ya existe");
    }
    if (clienteRepository.findByIdentificacion(identificacion).isPresent()) {
      throw new ClienteDuplicadoException("identificacion ya existe");
    }
  }

  private void ensureIdentificationAvailable(String identificacion, String currentClienteId) {
    boolean belongsToAnotherCustomer = clienteRepository.findByIdentificacion(identificacion)
        .filter(existing -> !existing.getClienteId().equals(currentClienteId)).isPresent();
    if (belongsToAnotherCustomer) {
      throw new ClienteDuplicadoException("identificacion ya existe");
    }
  }

  private boolean hasConflictingClienteId(String currentClienteId, String requestedClienteId) {
    return !currentClienteId.equals(requestedClienteId)
        && clienteRepository.existsByClienteId(requestedClienteId);
  }

  private Cliente toDomain(CreateClienteCommand command) {
    return new Cliente(
        command.clienteId(),
        new DatosPersona(
            command.nombre(), command.genero(), command.edad(), command.identificacion(),
            command.direccion(), command.telefono()),
        command.contrasena(),
        command.estado());
  }

  private Cliente toDomain(ReplaceClienteCommand command) {
    return new Cliente(
        command.clienteId(),
        new DatosPersona(
            command.nombre(), command.genero(), command.edad(), command.identificacion(),
            command.direccion(), command.telefono()),
        command.contrasena(),
        command.estado());
  }

  private Cliente mergePatch(Cliente current, PatchClienteCommand command) {
    return new Cliente(
        current.getClienteId(),
        new DatosPersona(
            valueOrCurrent(command.nombre(), current.getNombre()),
            valueOrCurrent(command.genero(), current.getGenero()),
            valueOrCurrent(command.edad(), current.getEdad()),
            valueOrCurrent(command.identificacion(), current.getIdentificacion()),
            valueOrCurrent(command.direccion(), current.getDireccion()),
            valueOrCurrent(command.telefono(), current.getTelefono())),
        valueOrCurrent(command.contrasena(), current.getContrasena()),
        valueOrCurrent(command.estado(), current.isEstado()));
  }

  private <T> T valueOrCurrent(T value, T current) {
    return value != null ? value : current;
  }

  private void publish(ClienteEventType eventType, Cliente cliente) {
    clienteEventPublisher.publish(ClienteEvent.from(eventType, cliente));
  }
}

package com.devsu.customerservice.application.usecase;

import com.devsu.customerservice.application.dto.ClientePatchRequest;
import com.devsu.customerservice.application.dto.ClienteRequest;
import com.devsu.customerservice.application.dto.ClienteResponse;
import com.devsu.customerservice.application.event.ClienteEvent;
import com.devsu.customerservice.application.event.ClienteEventPublisher;
import com.devsu.customerservice.application.event.ClienteEventType;
import com.devsu.customerservice.application.mapper.ClienteMapper;
import com.devsu.customerservice.domain.exception.ClienteDuplicadoException;
import com.devsu.customerservice.domain.exception.ClienteNoEncontradoException;
import com.devsu.customerservice.domain.model.Cliente;
import com.devsu.customerservice.domain.repository.ClienteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Coordinates customer application rules and transaction boundaries.
 */
@RequiredArgsConstructor
@Service
public class ClienteUseCase {

  private final ClienteRepository clienteRepository;
  private final ClienteMapper clienteMapper;
  private final ClienteEventPublisher clienteEventPublisher;

  /**
   * Creates a customer after validating customer identifier and identification uniqueness.
   */
  @Transactional
  public ClienteResponse create(ClienteRequest request) {
    ensureUniqueForCreate(request.clienteId(), request.identificacion());
    Cliente saved = clienteRepository.save(clienteMapper.toDomain(request));
    publish(ClienteEventType.CLIENTE_CREADO, saved);
    return clienteMapper.toResponse(saved);
  }

  /**
   * Returns the customer associated with the provided business identifier.
   */
  @Transactional(readOnly = true)
  public ClienteResponse get(String clienteId) {
    return clienteMapper.toResponse(find(clienteId));
  }

  /**
   * Returns every registered customer.
   */
  @Transactional(readOnly = true)
  public List<ClienteResponse> list() {
    return clienteRepository.findAll().stream().map(clienteMapper::toResponse).toList();
  }

  /**
   * Replaces an existing customer with a complete customer representation.
   */
  @Transactional
  public ClienteResponse replace(String clienteId, ClienteRequest request) {
    find(clienteId);
    if (hasConflictingClienteId(clienteId, request.clienteId())) {
      throw new ClienteDuplicadoException("clienteId ya existe");
    }
    ensureIdentificationAvailable(request.identificacion(), clienteId);
    Cliente saved = clienteRepository.save(clienteMapper.toDomain(request));
    publish(ClienteEventType.CLIENTE_ACTUALIZADO, saved);
    return clienteMapper.toResponse(saved);
  }

  /**
   * Applies the provided non-null customer fields to an existing customer.
   */
  @Transactional
  public ClienteResponse patch(String clienteId, ClientePatchRequest request) {
    Cliente current = find(clienteId);
    Cliente updated = clienteMapper.mergePatch(current, request);
    ensureIdentificationAvailable(updated.getIdentificacion(), clienteId);

    Cliente saved = clienteRepository.save(updated);
    publish(ClienteEventType.CLIENTE_ACTUALIZADO, saved);
    return clienteMapper.toResponse(saved);
  }

  /**
   * Marks a customer as inactive.
   */
  @Transactional
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
    return !currentClienteId.equals(requestedClienteId) && clienteRepository.existsByClienteId(
        requestedClienteId);
  }

  private void publish(ClienteEventType eventType, Cliente cliente) {
    clienteEventPublisher.publish(ClienteEvent.from(eventType, cliente));
  }
}

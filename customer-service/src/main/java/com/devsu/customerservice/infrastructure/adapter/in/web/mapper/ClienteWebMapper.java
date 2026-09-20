package com.devsu.customerservice.infrastructure.adapter.in.web.mapper;

import com.devsu.customerservice.application.command.CreateClienteCommand;
import com.devsu.customerservice.application.command.PatchClienteCommand;
import com.devsu.customerservice.application.command.ReplaceClienteCommand;
import com.devsu.customerservice.application.result.ClienteResult;
import com.devsu.customerservice.infrastructure.adapter.in.web.dto.ClientePatchRequest;
import com.devsu.customerservice.infrastructure.adapter.in.web.dto.ClienteRequest;
import com.devsu.customerservice.infrastructure.adapter.in.web.dto.ClienteResponse;
import java.util.List;
import org.springframework.stereotype.Component;

/** Maps HTTP DTOs to application commands and results. */
@Component
public class ClienteWebMapper {

  /** Converts a validated HTTP create request to an application command. */
  public CreateClienteCommand toCreateCommand(ClienteRequest request) {
    return new CreateClienteCommand(
        request.clienteId(), request.nombre(), request.genero(), request.edad(),
        request.identificacion(), request.direccion(), request.telefono(),
        request.contrasena(), request.estado());
  }

  /** Converts a validated HTTP replacement request to an application command. */
  public ReplaceClienteCommand toReplaceCommand(ClienteRequest request) {
    return new ReplaceClienteCommand(
        request.clienteId(), request.nombre(), request.genero(), request.edad(),
        request.identificacion(), request.direccion(), request.telefono(),
        request.contrasena(), request.estado());
  }

  /** Converts an HTTP patch request to an application command. */
  public PatchClienteCommand toPatchCommand(ClientePatchRequest request) {
    return new PatchClienteCommand(
        request.nombre(), request.genero(), request.edad(), request.identificacion(),
        request.direccion(), request.telefono(), request.contrasena(), request.estado());
  }

  /** Converts an application result to an HTTP response DTO. */
  public ClienteResponse toResponse(ClienteResult result) {
    return new ClienteResponse(
        result.clienteId(), result.nombre(), result.genero(), result.edad(),
        result.identificacion(), result.direccion(), result.telefono(), result.estado());
  }

  /** Converts a list of application results to HTTP response DTOs. */
  public List<ClienteResponse> toResponses(List<ClienteResult> results) {
    return results.stream().map(this::toResponse).toList();
  }
}

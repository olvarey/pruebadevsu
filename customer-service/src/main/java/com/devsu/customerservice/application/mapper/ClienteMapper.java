package com.devsu.customerservice.application.mapper;

import com.devsu.customerservice.application.dto.ClientePatchRequest;
import com.devsu.customerservice.application.dto.ClienteRequest;
import com.devsu.customerservice.application.dto.ClienteResponse;
import com.devsu.customerservice.domain.model.Cliente;
import com.devsu.customerservice.domain.model.DatosPersona;
import org.mapstruct.Mapper;

/** Converts customer DTOs and domain objects without leaking infrastructure types. */
@Mapper(componentModel = "spring")
public interface ClienteMapper {

  /** Builds a customer domain object from a validated create or replace request. */
  default Cliente toDomain(ClienteRequest request) {
    return new Cliente(
        request.clienteId(),
        toDatosPersona(request),
        request.contrasena(),
        request.estado());
  }

  /** Builds the API response for a customer domain object. */
  ClienteResponse toResponse(Cliente cliente);

  /** Applies a patch request over the current customer and returns a new domain object. */
  default Cliente mergePatch(Cliente current, ClientePatchRequest request) {
    return new Cliente(
        current.getClienteId(),
        patchDatosPersona(current, request),
        valueOrCurrent(request.contrasena(), current.getContrasena()),
        valueOrCurrent(request.estado(), current.isEstado()));
  }

  /** Builds personal data from a complete customer request. */
  default DatosPersona toDatosPersona(ClienteRequest request) {
    return new DatosPersona(
        request.nombre(),
        request.genero(),
        request.edad(),
        request.identificacion(),
        request.direccion(),
        request.telefono());
  }

  /** Builds personal data by applying a patch request over the current customer. */
  default DatosPersona patchDatosPersona(Cliente current, ClientePatchRequest request) {
    return new DatosPersona(
        valueOrCurrent(request.nombre(), current.getNombre()),
        valueOrCurrent(request.genero(), current.getGenero()),
        valueOrCurrent(request.edad(), current.getEdad()),
        valueOrCurrent(request.identificacion(), current.getIdentificacion()),
        valueOrCurrent(request.direccion(), current.getDireccion()),
        valueOrCurrent(request.telefono(), current.getTelefono()));
  }

  /** Returns the patch value when provided; otherwise keeps the current value. */
  default <T> T valueOrCurrent(T value, T current) {
    return value != null ? value : current;
  }
}

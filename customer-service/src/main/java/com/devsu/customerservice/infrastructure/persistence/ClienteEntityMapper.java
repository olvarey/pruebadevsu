package com.devsu.customerservice.infrastructure.persistence;

import com.devsu.customerservice.domain.model.Cliente;
import com.devsu.customerservice.domain.model.DatosPersona;
import org.mapstruct.Mapper;

/** Converts between customer domain objects and JPA entities. */
@Mapper(componentModel = "spring")
public interface ClienteEntityMapper {

  /** Builds a JPA entity from the customer aggregate. */
  ClienteEntity toEntity(Cliente cliente);

  /** Builds a customer aggregate from a JPA entity. */
  default Cliente toDomain(ClienteEntity entity) {
    return new Cliente(
        entity.getClienteId(),
        new DatosPersona(
            entity.getNombre(),
            entity.getGenero(),
            entity.getEdad(),
            entity.getIdentificacion(),
            entity.getDireccion(),
            entity.getTelefono()),
        entity.getContrasena(),
        entity.isEstado());
  }
}

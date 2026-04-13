package com.devsu.accountservice.infrastructure.persistence.mapper;

import com.devsu.accountservice.domain.model.Cuenta;
import com.devsu.accountservice.domain.model.DatosCuenta;
import com.devsu.accountservice.infrastructure.persistence.entity.CuentaEntity;
import org.mapstruct.Mapper;

/** Converts account domain objects to and from JPA entities. */
@Mapper(componentModel = "spring")
public interface CuentaEntityMapper {

  /** Builds the account domain object from a persistence entity. */
  default Cuenta toDomain(CuentaEntity entity) {
    return new Cuenta(
        entity.getNumeroCuenta(),
        new DatosCuenta(
            entity.getTipoCuenta(),
            entity.getSaldoInicial(),
            entity.getSaldoDisponible(),
            entity.isEstado(),
            entity.getClienteId()));
  }

  /** Builds the JPA entity from an account domain object. */
  CuentaEntity toEntity(Cuenta cuenta);
}

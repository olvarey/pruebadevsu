package com.devsu.accountservice.infrastructure.persistence.mapper;

import com.devsu.accountservice.domain.model.DatosMovimiento;
import com.devsu.accountservice.domain.model.Movimiento;
import com.devsu.accountservice.infrastructure.persistence.entity.MovimientoEntity;
import org.mapstruct.Mapper;

/** Converts movement domain objects to and from JPA entities. */
@Mapper(componentModel = "spring")
public interface MovimientoEntityMapper {

  /** Builds the movement domain object from a persistence entity. */
  default Movimiento toDomain(MovimientoEntity entity) {
    return new Movimiento(
        entity.getMovimientoId(),
        new DatosMovimiento(
            entity.getNumeroCuenta(),
            entity.getFecha(),
            entity.getTipoMovimiento(),
            entity.getValor(),
            entity.getSaldo()));
  }

  /** Builds the JPA entity from a movement domain object. */
  MovimientoEntity toEntity(Movimiento movimiento);
}

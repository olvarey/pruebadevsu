package com.devsu.accountservice.application.mapper;

import com.devsu.accountservice.application.dto.MovimientoPatchRequest;
import com.devsu.accountservice.application.dto.MovimientoRequest;
import com.devsu.accountservice.application.dto.MovimientoResponse;
import com.devsu.accountservice.domain.model.DatosMovimiento;
import com.devsu.accountservice.domain.model.Movimiento;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.mapstruct.Mapper;

/** Converts movement DTOs and domain objects without leaking infrastructure types. */
@Mapper(componentModel = "spring")
public interface MovimientoMapper {

  /** Builds a movement domain object after the account balance has been calculated. */
  default Movimiento toDomain(MovimientoRequest request, BigDecimal saldo) {
    BigDecimal valor = request.valor();
    return new Movimiento(
        UUID.randomUUID().toString(),
        new DatosMovimiento(
            request.numeroCuenta(),
            request.fecha(),
            DatosMovimiento.tipoPara(valor),
            valor,
            saldo));
  }

  /** Builds the API response for a movement domain object. */
  MovimientoResponse toResponse(Movimiento movimiento);

  /** Applies a partial update over the current movement. */
  default Movimiento mergePatch(
      Movimiento current, MovimientoPatchRequest request, BigDecimal saldo) {
    LocalDateTime fecha = valueOrCurrent(request.fecha(), current.getFecha());
    BigDecimal valor = valueOrCurrent(request.valor(), current.getValor());
    return current.actualizar(fecha, valor, saldo);
  }

  /** Returns the patch value when provided; otherwise keeps the current value. */
  default <T> T valueOrCurrent(T value, T current) {
    return value != null ? value : current;
  }
}

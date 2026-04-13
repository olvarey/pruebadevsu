package com.devsu.accountservice.application.mapper;

import com.devsu.accountservice.application.dto.CuentaPatchRequest;
import com.devsu.accountservice.application.dto.CuentaRequest;
import com.devsu.accountservice.application.dto.CuentaResponse;
import com.devsu.accountservice.domain.model.Cuenta;
import com.devsu.accountservice.domain.model.DatosCuenta;
import org.mapstruct.Mapper;

/** Converts account DTOs and domain objects without leaking infrastructure types. */
@Mapper(componentModel = "spring")
public interface CuentaMapper {

  /** Builds an account domain object from a validated create request. */
  default Cuenta toDomain(CuentaRequest request) {
    return new Cuenta(
        request.numeroCuenta(),
        new DatosCuenta(
            request.tipoCuenta(),
            request.saldoInicial(),
            request.saldoInicial(),
            request.estado(),
            request.clienteId()));
  }

  /** Builds the API response for an account domain object. */
  CuentaResponse toResponse(Cuenta cuenta);

  /** Replaces editable account data while preserving the current available balance. */
  default Cuenta replace(Cuenta current, CuentaRequest request) {
    return current.actualizarDatos(
        request.tipoCuenta(),
        request.saldoInicial(),
        request.estado(),
        request.clienteId());
  }

  /** Applies a partial update over the current account. */
  default Cuenta mergePatch(Cuenta current, CuentaPatchRequest request) {
    return current.actualizarDatos(
        valueOrCurrent(request.tipoCuenta(), current.getTipoCuenta()),
        valueOrCurrent(request.saldoInicial(), current.getSaldoInicial()),
        valueOrCurrent(request.estado(), current.isEstado()),
        valueOrCurrent(request.clienteId(), current.getClienteId()));
  }

  /** Returns the patch value when provided; otherwise keeps the current value. */
  default <T> T valueOrCurrent(T value, T current) {
    return value != null ? value : current;
  }
}

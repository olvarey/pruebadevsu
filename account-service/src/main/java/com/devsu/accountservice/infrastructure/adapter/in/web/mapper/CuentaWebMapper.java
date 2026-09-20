package com.devsu.accountservice.infrastructure.adapter.in.web.mapper;

import com.devsu.accountservice.application.command.CreateCuentaCommand;
import com.devsu.accountservice.application.command.PatchCuentaCommand;
import com.devsu.accountservice.application.command.ReplaceCuentaCommand;
import com.devsu.accountservice.application.result.CuentaResult;
import com.devsu.accountservice.infrastructure.adapter.in.web.dto.CuentaPatchRequest;
import com.devsu.accountservice.infrastructure.adapter.in.web.dto.CuentaRequest;
import com.devsu.accountservice.infrastructure.adapter.in.web.dto.CuentaResponse;
import java.util.List;
import org.springframework.stereotype.Component;

/** Maps account HTTP DTOs to application commands and results. */
@Component
public class CuentaWebMapper {

  /** Converts a validated create request to an application command. */
  public CreateCuentaCommand toCreateCommand(CuentaRequest request) {
    return new CreateCuentaCommand(request.numeroCuenta(), request.tipoCuenta(), request.saldoInicial(),
        request.estado(), request.clienteId());
  }

  /** Converts a validated replacement request to an application command. */
  public ReplaceCuentaCommand toReplaceCommand(CuentaRequest request) {
    return new ReplaceCuentaCommand(request.numeroCuenta(), request.tipoCuenta(), request.saldoInicial(),
        request.estado(), request.clienteId());
  }

  /** Converts a patch request to an application command. */
  public PatchCuentaCommand toPatchCommand(CuentaPatchRequest request) {
    return new PatchCuentaCommand(request.tipoCuenta(), request.saldoInicial(), request.estado(), request.clienteId());
  }

  /** Converts an application result to an HTTP response. */
  public CuentaResponse toResponse(CuentaResult result) {
    return new CuentaResponse(result.numeroCuenta(), result.tipoCuenta(), result.saldoInicial(),
        result.saldoDisponible(), result.estado(), result.clienteId());
  }

  /** Converts application results to HTTP responses. */
  public List<CuentaResponse> toResponses(List<CuentaResult> results) {
    return results.stream().map(this::toResponse).toList();
  }
}

package com.devsu.accountservice.infrastructure.adapter.in.web.mapper;

import com.devsu.accountservice.application.command.CreateMovimientoCommand;
import com.devsu.accountservice.application.command.PatchMovimientoCommand;
import com.devsu.accountservice.application.command.ReplaceMovimientoCommand;
import com.devsu.accountservice.application.result.MovimientoResult;
import com.devsu.accountservice.infrastructure.adapter.in.web.dto.MovimientoPatchRequest;
import com.devsu.accountservice.infrastructure.adapter.in.web.dto.MovimientoRequest;
import com.devsu.accountservice.infrastructure.adapter.in.web.dto.MovimientoResponse;
import java.util.List;
import org.springframework.stereotype.Component;

/** Maps movement HTTP DTOs to application commands and results. */
@Component
public class MovimientoWebMapper {

  /** Converts a create request to an application command. */
  public CreateMovimientoCommand toCreateCommand(MovimientoRequest request) {
    return new CreateMovimientoCommand(request.numeroCuenta(), request.fecha(), request.valor());
  }

  /** Converts a replacement request to an application command. */
  public ReplaceMovimientoCommand toReplaceCommand(MovimientoRequest request) {
    return new ReplaceMovimientoCommand(request.numeroCuenta(), request.fecha(), request.valor());
  }

  /** Converts a patch request to an application command. */
  public PatchMovimientoCommand toPatchCommand(MovimientoPatchRequest request) {
    return new PatchMovimientoCommand(request.fecha(), request.valor());
  }

  /** Converts an application result to an HTTP response. */
  public MovimientoResponse toResponse(MovimientoResult result) {
    return new MovimientoResponse(result.movimientoId(), result.numeroCuenta(), result.fecha(),
        result.tipoMovimiento(), result.valor(), result.saldo());
  }

  /** Converts application results to HTTP responses. */
  public List<MovimientoResponse> toResponses(List<MovimientoResult> results) {
    return results.stream().map(this::toResponse).toList();
  }
}

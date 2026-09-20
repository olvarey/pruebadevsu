package com.devsu.accountservice.infrastructure.adapter.in.web;

import com.devsu.accountservice.application.port.in.MovimientoInputPort;
import com.devsu.accountservice.infrastructure.adapter.in.web.dto.MovimientoPatchRequest;
import com.devsu.accountservice.infrastructure.adapter.in.web.dto.MovimientoRequest;
import com.devsu.accountservice.infrastructure.adapter.in.web.dto.MovimientoResponse;
import com.devsu.accountservice.infrastructure.adapter.in.web.mapper.MovimientoWebMapper;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for the movement CRUD API. */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

  private final MovimientoInputPort movimientoInputPort;
  private final MovimientoWebMapper movimientoWebMapper;

  /** Creates a movement and updates its account balance. */
  @PostMapping
  ResponseEntity<ApiResponse<MovimientoResponse>> create(
      @Valid @RequestBody MovimientoRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("Movimiento creado exitosamente",
            movimientoWebMapper.toResponse(movimientoInputPort.create(
                movimientoWebMapper.toCreateCommand(request)))));
  }

  /** Lists every registered movement. */
  @GetMapping
  ApiResponse<List<MovimientoResponse>> list() {
    return ApiResponse.success("Movimientos consultados exitosamente",
        movimientoWebMapper.toResponses(movimientoInputPort.list()));
  }

  /** Returns the movement matching the provided identifier. */
  @GetMapping("/{movimientoId}")
  ApiResponse<MovimientoResponse> get(@PathVariable String movimientoId) {
    return ApiResponse.success("Movimiento consultado exitosamente",
        movimientoWebMapper.toResponse(movimientoInputPort.get(movimientoId)));
  }

  /** Replaces editable movement data using a complete movement representation. */
  @PutMapping("/{movimientoId}")
  ApiResponse<MovimientoResponse> replace(
      @PathVariable String movimientoId, @Valid @RequestBody MovimientoRequest request) {
    return ApiResponse.success("Movimiento actualizado exitosamente",
        movimientoWebMapper.toResponse(movimientoInputPort.replace(movimientoId,
            movimientoWebMapper.toReplaceCommand(request))));
  }

  /** Applies a partial update to a movement. */
  @PatchMapping("/{movimientoId}")
  ApiResponse<MovimientoResponse> patch(
      @PathVariable String movimientoId, @Valid @RequestBody MovimientoPatchRequest request) {
    return ApiResponse.success("Movimiento actualizado exitosamente",
        movimientoWebMapper.toResponse(movimientoInputPort.patch(movimientoId,
            movimientoWebMapper.toPatchCommand(request))));
  }
}

package com.devsu.accountservice.infrastructure.web;

import com.devsu.accountservice.application.dto.MovimientoPatchRequest;
import com.devsu.accountservice.application.dto.MovimientoRequest;
import com.devsu.accountservice.application.dto.MovimientoResponse;
import com.devsu.accountservice.application.usecase.MovimientoUseCase;
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

/** REST controller for the movement CRU API. */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

  private final MovimientoUseCase movimientoUseCase;

  /** Creates a movement and updates its account balance. */
  @PostMapping
  ResponseEntity<ApiResponse<MovimientoResponse>> create(
      @Valid @RequestBody MovimientoRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            ApiResponse.success(
                "Movimiento creado exitosamente", movimientoUseCase.create(request)));
  }

  /** Lists every registered movement. */
  @GetMapping
  ApiResponse<List<MovimientoResponse>> list() {
    return ApiResponse.success("Movimientos consultados exitosamente", movimientoUseCase.list());
  }

  /** Returns the movement matching the provided identifier. */
  @GetMapping("/{movimientoId}")
  ApiResponse<MovimientoResponse> get(@PathVariable String movimientoId) {
    return ApiResponse.success(
        "Movimiento consultado exitosamente", movimientoUseCase.get(movimientoId));
  }

  /** Replaces editable movement data using a complete movement representation. */
  @PutMapping("/{movimientoId}")
  ApiResponse<MovimientoResponse> replace(
      @PathVariable String movimientoId, @Valid @RequestBody MovimientoRequest request) {
    return ApiResponse.success(
        "Movimiento actualizado exitosamente",
        movimientoUseCase.replace(movimientoId, request));
  }

  /** Applies a partial update to a movement. */
  @PatchMapping("/{movimientoId}")
  ApiResponse<MovimientoResponse> patch(
      @PathVariable String movimientoId, @Valid @RequestBody MovimientoPatchRequest request) {
    return ApiResponse.success(
        "Movimiento actualizado exitosamente", movimientoUseCase.patch(movimientoId, request));
  }
}

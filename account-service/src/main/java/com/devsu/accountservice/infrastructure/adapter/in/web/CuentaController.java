package com.devsu.accountservice.infrastructure.adapter.in.web;

import com.devsu.accountservice.application.port.in.CuentaInputPort;
import com.devsu.accountservice.infrastructure.adapter.in.web.dto.CuentaPatchRequest;
import com.devsu.accountservice.infrastructure.adapter.in.web.dto.CuentaRequest;
import com.devsu.accountservice.infrastructure.adapter.in.web.dto.CuentaResponse;
import com.devsu.accountservice.infrastructure.adapter.in.web.mapper.CuentaWebMapper;
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

/** REST controller for the account CRUD API. */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

  private final CuentaInputPort cuentaInputPort;
  private final CuentaWebMapper cuentaWebMapper;

  /** Creates an account from the provided request body. */
  @PostMapping
  ResponseEntity<ApiResponse<CuentaResponse>> create(@Valid @RequestBody CuentaRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("Cuenta creada exitosamente",
            cuentaWebMapper.toResponse(cuentaInputPort.create(cuentaWebMapper.toCreateCommand(request)))));
  }

  /** Lists every registered account. */
  @GetMapping
  ApiResponse<List<CuentaResponse>> list() {
    return ApiResponse.success("Cuentas consultadas exitosamente",
        cuentaWebMapper.toResponses(cuentaInputPort.list()));
  }

  /** Returns the account matching the provided account number. */
  @GetMapping("/{numeroCuenta}")
  ApiResponse<CuentaResponse> get(@PathVariable String numeroCuenta) {
    return ApiResponse.success("Cuenta consultada exitosamente",
        cuentaWebMapper.toResponse(cuentaInputPort.get(numeroCuenta)));
  }

  /** Replaces editable account data using a complete account representation. */
  @PutMapping("/{numeroCuenta}")
  ApiResponse<CuentaResponse> replace(
      @PathVariable String numeroCuenta, @Valid @RequestBody CuentaRequest request) {
    return ApiResponse.success("Cuenta actualizada exitosamente",
        cuentaWebMapper.toResponse(cuentaInputPort.replace(numeroCuenta,
            cuentaWebMapper.toReplaceCommand(request))));
  }

  /** Applies a partial update to an account. */
  @PatchMapping("/{numeroCuenta}")
  ApiResponse<CuentaResponse> patch(
      @PathVariable String numeroCuenta, @Valid @RequestBody CuentaPatchRequest request) {
    return ApiResponse.success("Cuenta actualizada exitosamente",
        cuentaWebMapper.toResponse(cuentaInputPort.patch(numeroCuenta,
            cuentaWebMapper.toPatchCommand(request))));
  }
}

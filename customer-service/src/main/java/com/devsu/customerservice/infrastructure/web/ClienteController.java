package com.devsu.customerservice.infrastructure.web;

import com.devsu.customerservice.application.dto.ClientePatchRequest;
import com.devsu.customerservice.application.dto.ClienteRequest;
import com.devsu.customerservice.application.dto.ClienteResponse;
import com.devsu.customerservice.application.usecase.ClienteUseCase;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the customer CRUD API.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

  private final ClienteUseCase clienteUseCase;

  /**
   * Creates a customer from the provided request body.
   */
  @PostMapping
  ResponseEntity<ApiResponse<ClienteResponse>> create(@Valid @RequestBody ClienteRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("Cliente creado exitosamente", clienteUseCase.create(request)));
  }

  /**
   * Lists every registered customer.
   */
  @GetMapping
  ApiResponse<List<ClienteResponse>> list() {
    return ApiResponse.success("Clientes consultados exitosamente", clienteUseCase.list());
  }

  /**
   * Returns the customer matching the provided business identifier.
   */
  @GetMapping("/{clienteId}")
  ApiResponse<ClienteResponse> get(@PathVariable String clienteId) {
    return ApiResponse.success("Cliente consultado exitosamente", clienteUseCase.get(clienteId));
  }

  /**
   * Replaces a customer using a complete customer representation.
   */
  @PutMapping("/{clienteId}")
  ApiResponse<ClienteResponse> replace(@PathVariable String clienteId,
      @Valid @RequestBody ClienteRequest request) {
    return ApiResponse.success("Cliente actualizado exitosamente",
        clienteUseCase.replace(clienteId, request));
  }

  /**
   * Applies a partial update to a customer.
   */
  @PatchMapping("/{clienteId}")
  ApiResponse<ClienteResponse> patch(@PathVariable String clienteId,
      @Valid @RequestBody ClientePatchRequest request) {
    return ApiResponse.success("Cliente actualizado exitosamente",
        clienteUseCase.patch(clienteId, request));
  }

  /**
   * Marks a customer as inactive.
   */
  @DeleteMapping("/{clienteId}")
  ApiResponse<Void> delete(@PathVariable String clienteId) {
    clienteUseCase.delete(clienteId);
    return ApiResponse.success("Cliente eliminado exitosamente");
  }
}

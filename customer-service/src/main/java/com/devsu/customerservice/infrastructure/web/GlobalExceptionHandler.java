package com.devsu.customerservice.infrastructure.web;

import com.devsu.customerservice.domain.exception.ClienteDuplicadoException;
import com.devsu.customerservice.domain.exception.ClienteNoEncontradoException;
import com.devsu.customerservice.domain.exception.DomainException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converts application and validation exceptions into API error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Returns a 404 response for missing customers.
   */
  @ExceptionHandler(ClienteNoEncontradoException.class)
  ResponseEntity<ApiResponse<Void>> notFound(RuntimeException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error(exception.getMessage()));
  }

  /**
   * Returns a 409 response for duplicated customer data.
   */
  @ExceptionHandler(ClienteDuplicadoException.class)
  ResponseEntity<ApiResponse<Void>> conflict(RuntimeException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiResponse.error(exception.getMessage()));
  }

  /**
   * Returns a 400 response for domain validation errors.
   */
  @ExceptionHandler(DomainException.class)
  ResponseEntity<ApiResponse<Void>> badRequest(RuntimeException exception) {
    return ResponseEntity.badRequest().body(ApiResponse.error(exception.getMessage()));
  }

  /**
   * Returns a 400 response with field-level validation errors.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiResponse<Void>> validation(MethodArgumentNotValidException exception) {
    return ResponseEntity.badRequest()
        .body(ApiResponse.error("Solicitud invalida", fieldErrors(exception)));
  }

  private Map<String, String> fieldErrors(MethodArgumentNotValidException exception) {
    Map<String, String> errors = new HashMap<>();
    exception.getBindingResult().getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    return errors;
  }
}

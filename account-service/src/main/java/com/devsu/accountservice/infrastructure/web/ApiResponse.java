package com.devsu.accountservice.infrastructure.web;

import java.time.Instant;
import java.util.Map;

/** Standard response envelope returned by the account REST API. */
public record ApiResponse<T>(
    Instant timestamp, boolean success, String message, T data, Map<String, String> errors) {

  /** Creates a successful response with payload data. */
  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(Instant.now(), true, message, data, Map.of());
  }

  /** Creates an error response without field-level validation details. */
  public static ApiResponse<Void> error(String message) {
    return new ApiResponse<>(Instant.now(), false, message, null, Map.of());
  }

  /** Creates an error response with field-level validation details. */
  public static ApiResponse<Void> error(String message, Map<String, String> errors) {
    return new ApiResponse<>(Instant.now(), false, message, null, errors);
  }
}

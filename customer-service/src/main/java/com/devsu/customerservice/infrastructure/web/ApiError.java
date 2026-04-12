package com.devsu.customerservice.infrastructure.web;

import java.time.Instant;
import java.util.Map;

/** Standard error response returned by customer API exception handlers. */
public record ApiError(Instant timestamp, int status, String message, Map<String, String> errors) {

  /** Creates an error response without field-level validation details. */
  public static ApiError of(int status, String message) {
    return new ApiError(Instant.now(), status, message, Map.of());
  }
}

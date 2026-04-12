package com.devsu.customerservice.domain.exception;

/** Base exception for domain validation errors. */
public class DomainException extends RuntimeException {

  /** Creates a domain exception with a user-facing validation message. */
  public DomainException(String message) {
    super(message);
  }
}

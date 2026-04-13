package com.devsu.accountservice.domain.exception;

/** Base exception for account domain rule violations. */
public class DomainException extends RuntimeException {

  /** Creates a domain exception with a user-facing message. */
  public DomainException(String message) {
    super(message);
  }
}

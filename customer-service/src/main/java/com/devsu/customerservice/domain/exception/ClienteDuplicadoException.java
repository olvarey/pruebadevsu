package com.devsu.customerservice.domain.exception;

/** Indicates that a customer unique field already exists. */
public class ClienteDuplicadoException extends RuntimeException {

  /** Creates an exception with the duplicated field detail. */
  public ClienteDuplicadoException(String message) {
    super(message);
  }
}

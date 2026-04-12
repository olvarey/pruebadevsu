package com.devsu.customerservice.domain.exception;

/** Indicates that no customer exists for the requested identifier. */
public class ClienteNoEncontradoException extends RuntimeException {

  /** Creates an exception for the missing customer identifier. */
  public ClienteNoEncontradoException(String clienteId) {
    super("Cliente no encontrado: " + clienteId);
  }
}

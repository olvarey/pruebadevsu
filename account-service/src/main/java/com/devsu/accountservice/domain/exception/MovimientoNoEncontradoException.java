package com.devsu.accountservice.domain.exception;

/** Raised when a movement cannot be found by its identifier. */
public class MovimientoNoEncontradoException extends DomainException {

  /** Creates the missing movement exception. */
  public MovimientoNoEncontradoException(String movimientoId) {
    super("El movimiento " + movimientoId + " no existe");
  }
}

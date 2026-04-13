package com.devsu.accountservice.domain.exception;

/** Raised when an account cannot be found by its number. */
public class CuentaNoEncontradaException extends DomainException {

  /** Creates the missing account exception. */
  public CuentaNoEncontradaException(String numeroCuenta) {
    super("La cuenta " + numeroCuenta + " no existe");
  }
}

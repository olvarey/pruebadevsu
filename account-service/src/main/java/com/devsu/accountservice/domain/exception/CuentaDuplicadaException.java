package com.devsu.accountservice.domain.exception;

/** Raised when an account number is already registered. */
public class CuentaDuplicadaException extends DomainException {

  /** Creates the duplicated account exception. */
  public CuentaDuplicadaException(String numeroCuenta) {
    super("La cuenta " + numeroCuenta + " ya existe");
  }
}

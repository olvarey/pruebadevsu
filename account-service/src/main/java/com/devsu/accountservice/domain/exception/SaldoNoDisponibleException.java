package com.devsu.accountservice.domain.exception;

/** Raised when an account movement would leave the account with a negative balance. */
public class SaldoNoDisponibleException extends DomainException {

  /** Creates the insufficient funds exception required by the challenge. */
  public SaldoNoDisponibleException() {
    super("Saldo no disponible");
  }
}

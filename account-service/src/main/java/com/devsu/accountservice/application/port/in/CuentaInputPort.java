package com.devsu.accountservice.application.port.in;

import com.devsu.accountservice.application.command.CreateCuentaCommand;
import com.devsu.accountservice.application.command.PatchCuentaCommand;
import com.devsu.accountservice.application.command.ReplaceCuentaCommand;
import com.devsu.accountservice.application.result.CuentaResult;
import java.util.List;

/** Inbound port for account use cases. */
public interface CuentaInputPort {

  /** Creates an account. */
  CuentaResult create(CreateCuentaCommand command);

  /** Finds an account by account number. */
  CuentaResult get(String numeroCuenta);

  /** Lists all accounts. */
  List<CuentaResult> list();

  /** Replaces editable account data. */
  CuentaResult replace(String numeroCuenta, ReplaceCuentaCommand command);

  /** Applies a partial account update. */
  CuentaResult patch(String numeroCuenta, PatchCuentaCommand command);
}

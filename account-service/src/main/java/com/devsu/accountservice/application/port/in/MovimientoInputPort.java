package com.devsu.accountservice.application.port.in;

import com.devsu.accountservice.application.command.CreateMovimientoCommand;
import com.devsu.accountservice.application.command.PatchMovimientoCommand;
import com.devsu.accountservice.application.command.ReplaceMovimientoCommand;
import com.devsu.accountservice.application.result.MovimientoResult;
import java.util.List;

/** Inbound port for movement use cases. */
public interface MovimientoInputPort {

  /** Creates a movement and updates its account balance. */
  MovimientoResult create(CreateMovimientoCommand command);

  /** Finds a movement by identifier. */
  MovimientoResult get(String movimientoId);

  /** Lists all movements. */
  List<MovimientoResult> list();

  /** Replaces movement data and adjusts the account balance. */
  MovimientoResult replace(String movimientoId, ReplaceMovimientoCommand command);

  /** Applies a partial movement update and adjusts the account balance. */
  MovimientoResult patch(String movimientoId, PatchMovimientoCommand command);
}

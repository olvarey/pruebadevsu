package com.devsu.accountservice.application.command;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Optional movement fields used for a partial update. */
public record PatchMovimientoCommand(LocalDateTime fecha, BigDecimal valor) {}

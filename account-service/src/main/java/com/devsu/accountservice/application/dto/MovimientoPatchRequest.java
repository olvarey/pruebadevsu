package com.devsu.accountservice.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Request body used to partially update a movement. */
public record MovimientoPatchRequest(LocalDateTime fecha, BigDecimal valor) {}

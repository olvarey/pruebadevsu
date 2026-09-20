package com.devsu.accountservice.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Request body used to partially update a movement. */
public record MovimientoPatchRequest(LocalDateTime fecha, BigDecimal valor) {}

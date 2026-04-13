package com.devsu.accountservice.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** JPA entity that stores movement data. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "movimientos")
public class MovimientoEntity {

  @Id
  @Column(name = "movimiento_id", nullable = false, length = 36)
  private String movimientoId;

  @Column(name = "numero_cuenta", nullable = false, length = 50)
  private String numeroCuenta;

  @Column(name = "fecha", nullable = false)
  private LocalDateTime fecha;

  @Column(name = "tipo_movimiento", nullable = false, length = 50)
  private String tipoMovimiento;

  @Column(name = "valor", nullable = false, precision = 19, scale = 2)
  private BigDecimal valor;

  @Column(name = "saldo", nullable = false, precision = 19, scale = 2)
  private BigDecimal saldo;
}

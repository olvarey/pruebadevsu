package com.devsu.accountservice.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** JPA entity that stores account data. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cuentas")
public class CuentaEntity {

  @Id
  @Column(name = "numero_cuenta", nullable = false, length = 50)
  private String numeroCuenta;

  @Column(name = "tipo_cuenta", nullable = false, length = 50)
  private String tipoCuenta;

  @Column(name = "saldo_inicial", nullable = false, precision = 19, scale = 2)
  private BigDecimal saldoInicial;

  @Column(name = "saldo_disponible", nullable = false, precision = 19, scale = 2)
  private BigDecimal saldoDisponible;

  @Column(name = "estado", nullable = false)
  private boolean estado;

  @Column(name = "cliente_id", nullable = false, length = 50)
  private String clienteId;
}

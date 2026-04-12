package com.devsu.customerservice.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** JPA entity that stores customer rows in the clientes table. */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PACKAGE)
@Table(name = "clientes")
public class ClienteEntity extends PersonaEntity {

  @Id
  @Column(name = "cliente_id", nullable = false, length = 60)
  private String clienteId;

  @Column(nullable = false)
  private String contrasena;

  @Column(nullable = false)
  private boolean estado;
}

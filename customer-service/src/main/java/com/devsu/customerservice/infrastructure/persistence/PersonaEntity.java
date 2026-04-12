package com.devsu.customerservice.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Base JPA mapping for the personal fields shared by customer persistence entities. */
@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PACKAGE)
public abstract class PersonaEntity {

  @Column(nullable = false)
  private String nombre;

  private String genero;

  private Integer edad;

  @Column(nullable = false, unique = true)
  private String identificacion;

  @Column(nullable = false)
  private String direccion;

  @Column(nullable = false)
  private String telefono;

}

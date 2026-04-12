package com.devsu.customerservice.domain.model;

/** Common personal data shared by customer-related domain models. */
public class Persona {

  private final String nombre;
  private final String genero;
  private final Integer edad;
  private final String identificacion;
  private final String direccion;
  private final String telefono;

  protected Persona(DatosPersona datosPersona) {
    this.nombre = datosPersona.nombre();
    this.genero = datosPersona.genero();
    this.edad = datosPersona.edad();
    this.identificacion = datosPersona.identificacion();
    this.direccion = datosPersona.direccion();
    this.telefono = datosPersona.telefono();
  }

  /** Returns the person's name. */
  public String getNombre() {
    return nombre;
  }

  /** Returns the person's gender when it was provided. */
  public String getGenero() {
    return genero;
  }

  /** Returns the person's age when it was provided. */
  public Integer getEdad() {
    return edad;
  }

  /** Returns the person's government identification value. */
  public String getIdentificacion() {
    return identificacion;
  }

  /** Returns the person's address. */
  public String getDireccion() {
    return direccion;
  }

  /** Returns the person's phone number. */
  public String getTelefono() {
    return telefono;
  }
}

package com.devsu.customerservice.domain.model;

import com.devsu.customerservice.domain.exception.DomainException;

/** Customer aggregate root with the login and status fields required by the challenge. */
public class Cliente extends Persona {

  private final String clienteId;
  private final String contrasena;
  private final boolean estado;

  /** Creates a customer and validates the required domain fields. */
  public Cliente(String clienteId, DatosPersona datosPersona, String contrasena, boolean estado) {
    super(datosPersona);
    this.clienteId = requireText(clienteId, "clienteId");
    this.contrasena = requireText(contrasena, "contrasena");
    this.estado = estado;
  }

  /** Returns a copy of this customer marked as inactive. */
  public Cliente desactivar() {
    return withEstado(false);
  }

  private Cliente withEstado(boolean nuevoEstado) {
    return new Cliente(
        clienteId,
        new DatosPersona(
            getNombre(),
            getGenero(),
            getEdad(),
            getIdentificacion(),
            getDireccion(),
            getTelefono()),
        contrasena,
        nuevoEstado);
  }

  /** Returns the business identifier used by customer endpoints. */
  public String getClienteId() {
    return clienteId;
  }

  /** Returns the customer's password value. */
  public String getContrasena() {
    return contrasena;
  }

  /** Returns whether the customer is active. */
  public boolean isEstado() {
    return estado;
  }

  private static String requireText(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new DomainException(field + " es requerido");
    }
    return value.trim();
  }
}

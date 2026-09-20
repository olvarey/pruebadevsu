package com.devsu.customerservice.application.result;

import com.devsu.customerservice.domain.model.Cliente;

/** Application result that exposes customer data without transport concerns. */
public record ClienteResult(
    String clienteId,
    String nombre,
    String genero,
    Integer edad,
    String identificacion,
    String direccion,
    String telefono,
    boolean estado) {

  /** Creates an application result from a customer aggregate. */
  public static ClienteResult from(Cliente cliente) {
    return new ClienteResult(
        cliente.getClienteId(),
        cliente.getNombre(),
        cliente.getGenero(),
        cliente.getEdad(),
        cliente.getIdentificacion(),
        cliente.getDireccion(),
        cliente.getTelefono(),
        cliente.isEstado());
  }
}

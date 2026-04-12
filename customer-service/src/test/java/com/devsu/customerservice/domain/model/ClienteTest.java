package com.devsu.customerservice.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.devsu.customerservice.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

class ClienteTest {

  @Test
  void createsValidClienteAndCanDeactivate() {
    Cliente cliente = new Cliente("CLI-001", validPersonalData(), "1234", true);

    Cliente inactive = cliente.desactivar();

    assertThat(cliente.isEstado()).isTrue();
    assertThat(inactive.isEstado()).isFalse();
    assertThat(inactive.getClienteId()).isEqualTo("CLI-001");
  }

  @Test
  void rejectsMissingRequiredFields() {
    DatosPersona datosPersona = validPersonalData();

    assertThatThrownBy(
            () -> new Cliente("", datosPersona, "1234", true))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("clienteId es requerido");
  }

  private DatosPersona validPersonalData() {
    return new DatosPersona(
        "Jose Lema", "M", 35, "10001", "Otavalo sn y principal", "098254785");
  }
}

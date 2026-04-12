package com.devsu.customerservice.domain.model;

import com.devsu.customerservice.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClienteTest {

    @Test
    void createsValidClienteAndCanDeactivate() {
        Cliente cliente = new Cliente(
                "CLI-001",
                "Jose Lema",
                "M",
                35,
                "10001",
                "Otavalo sn y principal",
                "098254785",
                "1234",
                true
        );

        Cliente inactive = cliente.desactivar();

        assertThat(cliente.estado()).isTrue();
        assertThat(inactive.estado()).isFalse();
        assertThat(inactive.clienteId()).isEqualTo("CLI-001");
    }

    @Test
    void rejectsMissingRequiredFields() {
        assertThatThrownBy(() -> new Cliente(
                "",
                "Jose Lema",
                "M",
                35,
                "10001",
                "Otavalo sn y principal",
                "098254785",
                "1234",
                true
        )).isInstanceOf(DomainException.class)
                .hasMessageContaining("clienteId es requerido");
    }
}

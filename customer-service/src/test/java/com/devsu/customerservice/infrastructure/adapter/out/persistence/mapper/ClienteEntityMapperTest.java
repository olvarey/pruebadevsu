package com.devsu.customerservice.infrastructure.adapter.out.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.devsu.customerservice.domain.model.Cliente;
import com.devsu.customerservice.domain.model.DatosPersona;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ClienteEntityMapperTest {

  private final ClienteEntityMapper mapper = Mappers.getMapper(ClienteEntityMapper.class);

  @Test
  void mapsCustomerToEntityAndBackWithoutLosingPersistenceFields() {
    Cliente customer = new Cliente(
        "CLI-001",
        new DatosPersona("Jose Lema", "M", 35, "10001", "Otavalo", "098"),
        "1234",
        true);

    var entity = mapper.toEntity(customer);
    var restored = mapper.toDomain(entity);

    assertThat(entity.getClienteId()).isEqualTo("CLI-001");
    assertThat(restored.getIdentificacion()).isEqualTo("10001");
    assertThat(restored.getContrasena()).isEqualTo("1234");
    assertThat(restored.isEstado()).isTrue();
  }
}

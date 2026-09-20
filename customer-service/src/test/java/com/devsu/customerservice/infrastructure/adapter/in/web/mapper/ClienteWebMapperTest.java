package com.devsu.customerservice.infrastructure.adapter.in.web.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.devsu.customerservice.application.result.ClienteResult;
import com.devsu.customerservice.infrastructure.adapter.in.web.dto.ClientePatchRequest;
import com.devsu.customerservice.infrastructure.adapter.in.web.dto.ClienteRequest;
import org.junit.jupiter.api.Test;

class ClienteWebMapperTest {

  private final ClienteWebMapper mapper = new ClienteWebMapper();

  @Test
  void mapsCompleteRequestToCreateAndReplaceCommands() {
    ClienteRequest request = new ClienteRequest(
        "CLI-001", "Jose Lema", "M", 35, "10001", "Otavalo", "098", "1234", true);

    assertThat(mapper.toCreateCommand(request).clienteId()).isEqualTo("CLI-001");
    assertThat(mapper.toReplaceCommand(request).contrasena()).isEqualTo("1234");
  }

  @Test
  void mapsPatchAndApplicationResultWithoutChangingFields() {
    ClientePatchRequest patch = new ClientePatchRequest(
        null, null, null, null, "Quito", "099", null, null);
    ClienteResult result = new ClienteResult(
        "CLI-001", "Jose Lema", "M", 35, "10001", "Quito", "099", true);

    assertThat(mapper.toPatchCommand(patch).direccion()).isEqualTo("Quito");
    assertThat(mapper.toResponse(result).clienteId()).isEqualTo("CLI-001");
    assertThat(mapper.toResponses(java.util.List.of(result))).hasSize(1);
  }
}

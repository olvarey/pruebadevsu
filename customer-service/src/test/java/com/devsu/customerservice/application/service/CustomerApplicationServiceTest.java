package com.devsu.customerservice.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.devsu.customerservice.application.command.CreateClienteCommand;
import com.devsu.customerservice.application.command.PatchClienteCommand;
import com.devsu.customerservice.application.command.ReplaceClienteCommand;
import com.devsu.customerservice.application.port.out.ClienteEvent;
import com.devsu.customerservice.application.port.out.ClienteEventPublisher;
import com.devsu.customerservice.application.port.out.ClienteRepository;
import com.devsu.customerservice.domain.exception.ClienteDuplicadoException;
import com.devsu.customerservice.domain.model.Cliente;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CustomerApplicationServiceTest {

  private final InMemoryClienteRepository repository = new InMemoryClienteRepository();
  private final RecordingEventPublisher publisher = new RecordingEventPublisher();
  private final CustomerApplicationService service =
      new CustomerApplicationService(repository, publisher);

  @Test
  void supportsCustomerOperationsWithoutSpringOrExternalAdapters() {
    var created = service.create(command("CLI-001", "10001"));

    assertThat(created.clienteId()).isEqualTo("CLI-001");
    assertThat(service.get("CLI-001").nombre()).isEqualTo("Jose Lema");
    assertThat(service.list()).hasSize(1);

    var replaced = service.replace(
        "CLI-001",
        new ReplaceClienteCommand(
            "CLI-001", "Ana Lema", "F", 30, "10001", "Quito", "099", "5678", true));
    assertThat(replaced.nombre()).isEqualTo("Ana Lema");

    var patched = service.patch(
        "CLI-001",
        new PatchClienteCommand(null, null, null, null, "Cuenca", null, null, null));
    assertThat(patched.direccion()).isEqualTo("Cuenca");

    service.delete("CLI-001");
    assertThat(service.get("CLI-001").estado()).isFalse();
    assertThat(publisher.events).hasSize(4);
  }

  @Test
  void rejectsDuplicateCustomerAndDoesNotPublishEvent() {
    service.create(command("CLI-001", "10001"));

    assertThatThrownBy(() -> service.create(command("CLI-002", "10001")))
        .isInstanceOf(ClienteDuplicadoException.class)
        .hasMessage("identificacion ya existe");

    assertThat(publisher.events).hasSize(1);
  }

  private CreateClienteCommand command(String clienteId, String identificacion) {
    return new CreateClienteCommand(
        clienteId, "Jose Lema", "M", 35, identificacion,
        "Otavalo", "098", "1234", true);
  }

  private static class RecordingEventPublisher implements ClienteEventPublisher {
    private final List<ClienteEvent> events = new ArrayList<>();

    @Override
    public void publish(ClienteEvent event) {
      events.add(event);
    }
  }

  private static class InMemoryClienteRepository implements ClienteRepository {
    private final List<Cliente> customers = new ArrayList<>();

    @Override
    public Cliente save(Cliente cliente) {
      customers.removeIf(existing -> existing.getClienteId().equals(cliente.getClienteId()));
      customers.add(cliente);
      return cliente;
    }

    @Override
    public Optional<Cliente> findByClienteId(String clienteId) {
      return customers.stream().filter(customer -> customer.getClienteId().equals(clienteId))
          .findFirst();
    }

    @Override
    public Optional<Cliente> findByIdentificacion(String identificacion) {
      return customers.stream()
          .filter(customer -> customer.getIdentificacion().equals(identificacion))
          .findFirst();
    }

    @Override
    public List<Cliente> findAll() {
      return List.copyOf(customers);
    }

    @Override
    public boolean existsByClienteId(String clienteId) {
      return findByClienteId(clienteId).isPresent();
    }
  }
}

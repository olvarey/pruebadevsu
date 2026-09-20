package com.devsu.accountservice.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.devsu.accountservice.application.command.CreateCuentaCommand;
import com.devsu.accountservice.application.command.PatchCuentaCommand;
import com.devsu.accountservice.application.command.ReplaceCuentaCommand;
import com.devsu.accountservice.application.result.CuentaResult;
import com.devsu.accountservice.application.port.out.CuentaRepository;
import com.devsu.accountservice.domain.exception.CuentaDuplicadaException;
import com.devsu.accountservice.domain.exception.CuentaNoEncontradaException;
import com.devsu.accountservice.domain.model.Cuenta;
import com.devsu.accountservice.domain.model.DatosCuenta;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Characterization tests for account application behavior before port migration. */
class AccountApplicationServiceTest {

  @Test
  void createRejectsDuplicateAccountNumbers() {
    InMemoryCuentaRepository repository = new InMemoryCuentaRepository();
    repository.save(account("478758", "2000.00", "CLI-001"));
    CuentaApplicationService useCase = new CuentaApplicationService(repository);

    assertThatThrownBy(() -> useCase.create(createCommand("478758", "CLI-001")))
        .isInstanceOf(CuentaDuplicadaException.class)
        .hasMessageContaining("478758");
  }

  @Test
  void getRejectsMissingAccount() {
    CuentaApplicationService useCase = new CuentaApplicationService(new InMemoryCuentaRepository());

    assertThatThrownBy(() -> useCase.get("missing"))
        .isInstanceOf(CuentaNoEncontradaException.class);
  }

  @Test
  void replacePreservesAvailableBalance() {
    InMemoryCuentaRepository repository = new InMemoryCuentaRepository();
    repository.save(account("478758", "1425.00", "CLI-001"));
    CuentaApplicationService useCase = new CuentaApplicationService(repository);

    CuentaResult response = useCase.replace("478758", replaceCommand("478758", "CLI-002"));

    assertThat(response.saldoDisponible()).isEqualByComparingTo("1425.00");
    assertThat(response.clienteId()).isEqualTo("CLI-002");
  }

  @Test
  void patchUpdatesOnlyProvidedAccountFields() {
    InMemoryCuentaRepository repository = new InMemoryCuentaRepository();
    repository.save(account("478758", "2000.00", "CLI-001"));
    CuentaApplicationService useCase = new CuentaApplicationService(repository);

    CuentaResult response = useCase.patch(
        "478758", new PatchCuentaCommand("Corriente", null, null, null));

    assertThat(response.tipoCuenta()).isEqualTo("Corriente");
    assertThat(response.saldoInicial()).isEqualByComparingTo("2000.00");
    assertThat(response.clienteId()).isEqualTo("CLI-001");
  }

  @Test
  void listReturnsAllPersistedAccounts() {
    InMemoryCuentaRepository repository = new InMemoryCuentaRepository();
    repository.save(account("478758", "2000.00", "CLI-001"));
    repository.save(account("225487", "100.00", "CLI-002"));

    assertThat(new CuentaApplicationService(repository).list())
        .extracting(CuentaResult::numeroCuenta)
        .containsExactly("478758", "225487");
  }

  private CreateCuentaCommand createCommand(String numeroCuenta, String clienteId) {
    return new CreateCuentaCommand(
        numeroCuenta, "Ahorro", new BigDecimal("2000.00"), true, clienteId);
  }

  private ReplaceCuentaCommand replaceCommand(String numeroCuenta, String clienteId) {
    return new ReplaceCuentaCommand(
        numeroCuenta, "Ahorro", new BigDecimal("2000.00"), true, clienteId);
  }

  private Cuenta account(String numeroCuenta, String saldo, String clienteId) {
    BigDecimal amount = new BigDecimal(saldo);
    return new Cuenta(
        numeroCuenta, new DatosCuenta("Ahorro", amount, amount, true, clienteId));
  }

  private static class InMemoryCuentaRepository implements CuentaRepository {

    private final List<Cuenta> cuentas = new ArrayList<>();

    @Override
    public boolean existsByNumeroCuenta(String numeroCuenta) {
      return findByNumeroCuenta(numeroCuenta).isPresent();
    }

    @Override
    public Optional<Cuenta> findByNumeroCuenta(String numeroCuenta) {
      return cuentas.stream().filter(cuenta -> cuenta.getNumeroCuenta().equals(numeroCuenta)).findFirst();
    }

    @Override
    public List<Cuenta> findAll() {
      return List.copyOf(cuentas);
    }

    @Override
    public List<Cuenta> findByClienteId(String clienteId) {
      return cuentas.stream().filter(cuenta -> cuenta.getClienteId().equals(clienteId)).toList();
    }

    @Override
    public Cuenta save(Cuenta cuenta) {
      findByNumeroCuenta(cuenta.getNumeroCuenta()).ifPresent(cuentas::remove);
      cuentas.add(cuenta);
      return cuenta;
    }
  }
}

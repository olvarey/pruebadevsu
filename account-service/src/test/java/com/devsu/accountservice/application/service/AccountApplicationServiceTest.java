package com.devsu.accountservice.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.devsu.accountservice.application.dto.CuentaPatchRequest;
import com.devsu.accountservice.application.dto.CuentaRequest;
import com.devsu.accountservice.application.dto.CuentaResponse;
import com.devsu.accountservice.application.mapper.CuentaMapper;
import com.devsu.accountservice.application.usecase.CuentaUseCase;
import com.devsu.accountservice.domain.exception.CuentaDuplicadaException;
import com.devsu.accountservice.domain.exception.CuentaNoEncontradaException;
import com.devsu.accountservice.domain.model.Cuenta;
import com.devsu.accountservice.domain.model.DatosCuenta;
import com.devsu.accountservice.domain.repository.CuentaRepository;
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
    CuentaUseCase useCase = new CuentaUseCase(repository, new TestCuentaMapper());

    assertThatThrownBy(() -> useCase.create(request("478758", "CLI-001")))
        .isInstanceOf(CuentaDuplicadaException.class)
        .hasMessageContaining("478758");
  }

  @Test
  void getRejectsMissingAccount() {
    CuentaUseCase useCase = new CuentaUseCase(new InMemoryCuentaRepository(), new TestCuentaMapper());

    assertThatThrownBy(() -> useCase.get("missing"))
        .isInstanceOf(CuentaNoEncontradaException.class);
  }

  @Test
  void replacePreservesAvailableBalance() {
    InMemoryCuentaRepository repository = new InMemoryCuentaRepository();
    repository.save(account("478758", "1425.00", "CLI-001"));
    CuentaUseCase useCase = new CuentaUseCase(repository, new TestCuentaMapper());

    CuentaResponse response =
        useCase.replace("478758", request("478758", "CLI-002"));

    assertThat(response.saldoDisponible()).isEqualByComparingTo("1425.00");
    assertThat(response.clienteId()).isEqualTo("CLI-002");
  }

  @Test
  void patchUpdatesOnlyProvidedAccountFields() {
    InMemoryCuentaRepository repository = new InMemoryCuentaRepository();
    repository.save(account("478758", "2000.00", "CLI-001"));
    CuentaUseCase useCase = new CuentaUseCase(repository, new TestCuentaMapper());

    CuentaResponse response =
        useCase.patch("478758", new CuentaPatchRequest("Corriente", null, null, null));

    assertThat(response.tipoCuenta()).isEqualTo("Corriente");
    assertThat(response.saldoInicial()).isEqualByComparingTo("2000.00");
    assertThat(response.clienteId()).isEqualTo("CLI-001");
  }

  @Test
  void listReturnsAllPersistedAccounts() {
    InMemoryCuentaRepository repository = new InMemoryCuentaRepository();
    repository.save(account("478758", "2000.00", "CLI-001"));
    repository.save(account("225487", "100.00", "CLI-002"));

    assertThat(new CuentaUseCase(repository, new TestCuentaMapper()).list())
        .extracting(CuentaResponse::numeroCuenta)
        .containsExactly("478758", "225487");
  }

  private CuentaRequest request(String numeroCuenta, String clienteId) {
    return new CuentaRequest(
        numeroCuenta, "Ahorro", new BigDecimal("2000.00"), true, clienteId);
  }

  private Cuenta account(String numeroCuenta, String saldo, String clienteId) {
    BigDecimal amount = new BigDecimal(saldo);
    return new Cuenta(
        numeroCuenta, new DatosCuenta("Ahorro", amount, amount, true, clienteId));
  }

  private static class TestCuentaMapper implements CuentaMapper {

    @Override
    public CuentaResponse toResponse(Cuenta cuenta) {
      return new CuentaResponse(
          cuenta.getNumeroCuenta(), cuenta.getTipoCuenta(), cuenta.getSaldoInicial(),
          cuenta.getSaldoDisponible(), cuenta.isEstado(), cuenta.getClienteId());
    }
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

package com.devsu.accountservice.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.devsu.accountservice.application.dto.MovimientoPatchRequest;
import com.devsu.accountservice.application.dto.MovimientoRequest;
import com.devsu.accountservice.application.dto.MovimientoResponse;
import com.devsu.accountservice.application.mapper.MovimientoMapper;
import com.devsu.accountservice.application.usecase.MovimientoUseCase;
import com.devsu.accountservice.domain.exception.CuentaNoEncontradaException;
import com.devsu.accountservice.domain.exception.MovimientoNoEncontradoException;
import com.devsu.accountservice.domain.exception.SaldoNoDisponibleException;
import com.devsu.accountservice.domain.model.Cuenta;
import com.devsu.accountservice.domain.model.DatosCuenta;
import com.devsu.accountservice.domain.model.DatosMovimiento;
import com.devsu.accountservice.domain.model.Movimiento;
import com.devsu.accountservice.domain.repository.CuentaRepository;
import com.devsu.accountservice.domain.repository.MovimientoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Characterization tests for movement application behavior before port migration. */
class MovementApplicationServiceTest {

  @Test
  void createUpdatesBalanceAndStoresMovement() {
    InMemoryCuentaRepository cuentas = new InMemoryCuentaRepository();
    InMemoryMovimientoRepository movimientos = new InMemoryMovimientoRepository();
    cuentas.save(account("100.00"));

    MovimientoResponse response = useCase(cuentas, movimientos).create(request("-25.00"));

    assertThat(cuentas.findByNumeroCuenta("478758").orElseThrow().getSaldoDisponible())
        .isEqualByComparingTo("75.00");
    assertThat(response.saldo()).isEqualByComparingTo("75.00");
    assertThat(movimientos.findAll()).hasSize(1);
  }

  @Test
  void insufficientWithdrawalDoesNotStoreBalanceOrMovement() {
    InMemoryCuentaRepository cuentas = new InMemoryCuentaRepository();
    InMemoryMovimientoRepository movimientos = new InMemoryMovimientoRepository();
    cuentas.save(account("100.00"));

    assertThatThrownBy(() -> useCase(cuentas, movimientos).create(request("-150.00")))
        .isInstanceOf(SaldoNoDisponibleException.class);
    assertThat(cuentas.findByNumeroCuenta("478758").orElseThrow().getSaldoDisponible())
        .isEqualByComparingTo("100.00");
    assertThat(movimientos.findAll()).isEmpty();
  }

  @Test
  void replaceAdjustsBalanceByMovementDelta() {
    InMemoryCuentaRepository cuentas = new InMemoryCuentaRepository();
    InMemoryMovimientoRepository movimientos = new InMemoryMovimientoRepository();
    cuentas.save(account("100.00"));
    Movimiento current = movement("-25.00", "75.00");
    movimientos.save(current);

    useCase(cuentas, movimientos).replace(
        current.getMovimientoId(), request("-10.00"));

    assertThat(cuentas.findByNumeroCuenta("478758").orElseThrow().getSaldoDisponible())
        .isEqualByComparingTo("115.00");
  }

  @Test
  void patchAdjustsBalanceByProvidedValueDelta() {
    InMemoryCuentaRepository cuentas = new InMemoryCuentaRepository();
    InMemoryMovimientoRepository movimientos = new InMemoryMovimientoRepository();
    cuentas.save(account("100.00"));
    Movimiento current = movement("-25.00", "75.00");
    movimientos.save(current);

    useCase(cuentas, movimientos).patch(
        current.getMovimientoId(), new MovimientoPatchRequest(null, new BigDecimal("-15.00")));

    assertThat(cuentas.findByNumeroCuenta("478758").orElseThrow().getSaldoDisponible())
        .isEqualByComparingTo("110.00");
  }

  @Test
  void createRejectsMissingAccount() {
    assertThatThrownBy(
            () -> useCase(new InMemoryCuentaRepository(), new InMemoryMovimientoRepository())
                .create(request("10.00")))
        .isInstanceOf(CuentaNoEncontradaException.class);
  }

  @Test
  void getRejectsMissingMovement() {
    assertThatThrownBy(
            () -> useCase(new InMemoryCuentaRepository(), new InMemoryMovimientoRepository())
                .get("missing"))
        .isInstanceOf(MovimientoNoEncontradoException.class);
  }

  @Test
  void replaceRejectsDifferentAccount() {
    InMemoryCuentaRepository cuentas = new InMemoryCuentaRepository();
    InMemoryMovimientoRepository movimientos = new InMemoryMovimientoRepository();
    cuentas.save(account("100.00"));
    Movimiento current = movement("-25.00", "75.00");
    movimientos.save(current);

    assertThatThrownBy(() -> useCase(cuentas, movimientos).replace(
            current.getMovimientoId(), new MovimientoRequest("other", null, new BigDecimal("-10.00"))))
        .isInstanceOf(CuentaNoEncontradaException.class);
  }

  private MovimientoUseCase useCase(
      InMemoryCuentaRepository cuentas, InMemoryMovimientoRepository movimientos) {
    return new MovimientoUseCase(cuentas, movimientos, new TestMovimientoMapper());
  }

  private MovimientoRequest request(String valor) {
    return new MovimientoRequest("478758", LocalDateTime.parse("2026-04-10T09:00:00"), new BigDecimal(valor));
  }

  private Cuenta account(String saldo) {
    BigDecimal amount = new BigDecimal(saldo);
    return new Cuenta("478758", new DatosCuenta("Ahorro", amount, amount, true, "CLI-001"));
  }

  private Movimiento movement(String valor, String saldo) {
    BigDecimal amount = new BigDecimal(valor);
    return new Movimiento(
        "MOV-001",
        new DatosMovimiento("478758", LocalDateTime.parse("2026-04-10T09:00:00"),
            DatosMovimiento.tipoPara(amount), amount, new BigDecimal(saldo)));
  }

  private static class TestMovimientoMapper implements MovimientoMapper {

    @Override
    public MovimientoResponse toResponse(Movimiento movimiento) {
      return new MovimientoResponse(
          movimiento.getMovimientoId(), movimiento.getNumeroCuenta(), movimiento.getFecha(),
          movimiento.getTipoMovimiento(), movimiento.getValor(), movimiento.getSaldo());
    }
  }

  private static class InMemoryCuentaRepository implements CuentaRepository {

    private final List<Cuenta> cuentas = new ArrayList<>();

    @Override
    public boolean existsByNumeroCuenta(String numeroCuenta) { return findByNumeroCuenta(numeroCuenta).isPresent(); }
    @Override
    public Optional<Cuenta> findByNumeroCuenta(String numeroCuenta) {
      return cuentas.stream().filter(cuenta -> cuenta.getNumeroCuenta().equals(numeroCuenta)).findFirst();
    }
    @Override
    public List<Cuenta> findAll() { return List.copyOf(cuentas); }
    @Override
    public List<Cuenta> findByClienteId(String clienteId) { return List.of(); }
    @Override
    public Cuenta save(Cuenta cuenta) {
      findByNumeroCuenta(cuenta.getNumeroCuenta()).ifPresent(cuentas::remove);
      cuentas.add(cuenta);
      return cuenta;
    }
  }

  private static class InMemoryMovimientoRepository implements MovimientoRepository {

    private final List<Movimiento> movimientos = new ArrayList<>();

    @Override
    public Optional<Movimiento> findByMovimientoId(String movimientoId) {
      return movimientos.stream().filter(movimiento -> movimiento.getMovimientoId().equals(movimientoId)).findFirst();
    }
    @Override
    public List<Movimiento> findAll() { return List.copyOf(movimientos); }
    @Override
    public List<Movimiento> findByNumeroCuentaInAndFechaBetween(
        List<String> numerosCuenta, LocalDateTime start, LocalDateTime end) { return List.of(); }
    @Override
    public Movimiento save(Movimiento movimiento) {
      findByMovimientoId(movimiento.getMovimientoId()).ifPresent(movimientos::remove);
      movimientos.add(movimiento);
      return movimiento;
    }
  }
}

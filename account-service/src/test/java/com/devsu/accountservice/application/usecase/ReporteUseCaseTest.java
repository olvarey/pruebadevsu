package com.devsu.accountservice.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import com.devsu.accountservice.application.dto.EstadoCuentaResponse;
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

/** Unit tests for account statement report generation. */
class ReporteUseCaseTest {

  @Test
  void getEstadoCuentaReturnsRowsInsideDateRangeForCustomerAccounts() {
    InMemoryCuentaRepository cuentaRepository = new InMemoryCuentaRepository();
    InMemoryMovimientoRepository movimientoRepository = new InMemoryMovimientoRepository();
    ReporteUseCase useCase = new ReporteUseCase(cuentaRepository, movimientoRepository);
    cuentaRepository.save(cuenta("478758", "CLI-001"));
    movimientoRepository.save(movimiento("MOV-001", "478758", "2026-04-10T09:00:00", "-575.00"));
    movimientoRepository.save(movimiento("MOV-002", "478758", "2026-04-12T09:00:00", "100.00"));

    List<EstadoCuentaResponse> response =
        useCase.getEstadoCuenta("2026-04-10,2026-04-10", "CLI-001");

    assertThat(response).hasSize(1);
    assertThat(response.get(0).numeroCuenta()).isEqualTo("478758");
    assertThat(response.get(0).cliente()).isEqualTo("CLI-001");
    assertThat(response.get(0).movimiento()).isEqualByComparingTo("-575.00");
  }

  private Cuenta cuenta(String numeroCuenta, String clienteId) {
    return new Cuenta(
        numeroCuenta,
        new DatosCuenta(
            "Ahorro", new BigDecimal("2000.00"), new BigDecimal("1525.00"), true, clienteId));
  }

  private Movimiento movimiento(
      String movimientoId, String numeroCuenta, String fecha, String valor) {
    BigDecimal movimientoValor = new BigDecimal(valor);
    return new Movimiento(
        movimientoId,
        new DatosMovimiento(
            numeroCuenta,
            LocalDateTime.parse(fecha),
            DatosMovimiento.tipoPara(movimientoValor),
            movimientoValor,
            new BigDecimal("1425.00")));
  }

  private static class InMemoryCuentaRepository implements CuentaRepository {

    private final List<Cuenta> cuentas = new ArrayList<>();

    @Override
    public boolean existsByNumeroCuenta(String numeroCuenta) {
      return cuentas.stream().anyMatch(cuenta -> cuenta.getNumeroCuenta().equals(numeroCuenta));
    }

    @Override
    public Optional<Cuenta> findByNumeroCuenta(String numeroCuenta) {
      return cuentas.stream()
          .filter(cuenta -> cuenta.getNumeroCuenta().equals(numeroCuenta))
          .findFirst();
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
      cuentas.add(cuenta);
      return cuenta;
    }
  }

  private static class InMemoryMovimientoRepository implements MovimientoRepository {

    private final List<Movimiento> movimientos = new ArrayList<>();

    @Override
    public Optional<Movimiento> findByMovimientoId(String movimientoId) {
      return movimientos.stream()
          .filter(movimiento -> movimiento.getMovimientoId().equals(movimientoId))
          .findFirst();
    }

    @Override
    public List<Movimiento> findAll() {
      return List.copyOf(movimientos);
    }

    @Override
    public List<Movimiento> findByNumeroCuentaInAndFechaBetween(
        List<String> numerosCuenta, LocalDateTime start, LocalDateTime end) {
      return movimientos.stream()
          .filter(movimiento -> numerosCuenta.contains(movimiento.getNumeroCuenta()))
          .filter(movimiento -> !movimiento.getFecha().isBefore(start))
          .filter(movimiento -> !movimiento.getFecha().isAfter(end))
          .toList();
    }

    @Override
    public Movimiento save(Movimiento movimiento) {
      movimientos.add(movimiento);
      return movimiento;
    }
  }
}

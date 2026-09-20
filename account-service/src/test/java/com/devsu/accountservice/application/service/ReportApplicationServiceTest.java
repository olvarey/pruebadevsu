package com.devsu.accountservice.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.devsu.accountservice.application.port.out.CuentaRepository;
import com.devsu.accountservice.application.port.out.MovimientoRepository;
import com.devsu.accountservice.application.result.EstadoCuentaResult;
import com.devsu.accountservice.domain.model.Cuenta;
import com.devsu.accountservice.domain.model.DatosCuenta;
import com.devsu.accountservice.domain.model.DatosMovimiento;
import com.devsu.accountservice.domain.model.Movimiento;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Characterization tests for report application behavior before port migration. */
class ReportApplicationServiceTest {

  @Test
  void reportIncludesInclusiveDatesInChronologicalOrderForCustomer() {
    InMemoryCuentaRepository cuentas = new InMemoryCuentaRepository();
    InMemoryMovimientoRepository movimientos = new InMemoryMovimientoRepository();
    cuentas.save(account("478758", "CLI-001"));
    movimientos.save(movement("MOV-002", "2026-04-12T09:00:00", "100.00"));
    movimientos.save(movement("MOV-001", "2026-04-10T09:00:00", "-575.00"));

    List<EstadoCuentaResult> response =
        new ReporteApplicationService(cuentas, movimientos)
            .getEstadoCuenta("2026-04-10,2026-04-12", "CLI-001");

    assertThat(response).extracting(EstadoCuentaResult::numeroCuenta).containsExactly("478758", "478758");
    assertThat(response).extracting(EstadoCuentaResult::movimiento)
        .containsExactly(new BigDecimal("-575.00"), new BigDecimal("100.00"));
  }

  @Test
  void reportReturnsEmptyListForCustomerWithoutAccounts() {
    List<EstadoCuentaResult> response =
        new ReporteApplicationService(new InMemoryCuentaRepository(), new InMemoryMovimientoRepository())
            .getEstadoCuenta("2026-04-10,2026-04-12", "CLI-404");

    assertThat(response).isEmpty();
  }

  private Cuenta account(String numeroCuenta, String clienteId) {
    return new Cuenta(numeroCuenta, new DatosCuenta("Ahorro", new BigDecimal("2000.00"),
        new BigDecimal("1425.00"), true, clienteId));
  }

  private Movimiento movement(String id, String fecha, String valor) {
    BigDecimal amount = new BigDecimal(valor);
    return new Movimiento(id, new DatosMovimiento("478758", LocalDateTime.parse(fecha),
        DatosMovimiento.tipoPara(amount), amount, new BigDecimal("1425.00")));
  }

  private static class InMemoryCuentaRepository implements CuentaRepository {
    private final List<Cuenta> cuentas = new ArrayList<>();
    @Override public boolean existsByNumeroCuenta(String numeroCuenta) { return findByNumeroCuenta(numeroCuenta).isPresent(); }
    @Override public Optional<Cuenta> findByNumeroCuenta(String numeroCuenta) { return cuentas.stream().filter(c -> c.getNumeroCuenta().equals(numeroCuenta)).findFirst(); }
    @Override public List<Cuenta> findAll() { return List.copyOf(cuentas); }
    @Override public List<Cuenta> findByClienteId(String clienteId) { return cuentas.stream().filter(c -> c.getClienteId().equals(clienteId)).toList(); }
    @Override public Cuenta save(Cuenta cuenta) { cuentas.add(cuenta); return cuenta; }
  }

  private static class InMemoryMovimientoRepository implements MovimientoRepository {
    private final List<Movimiento> movimientos = new ArrayList<>();
    @Override public Optional<Movimiento> findByMovimientoId(String id) { return movimientos.stream().filter(m -> m.getMovimientoId().equals(id)).findFirst(); }
    @Override public List<Movimiento> findAll() { return List.copyOf(movimientos); }
    @Override public List<Movimiento> findByNumeroCuentaInAndFechaBetween(List<String> accounts, LocalDateTime start, LocalDateTime end) {
      return movimientos.stream().filter(m -> accounts.contains(m.getNumeroCuenta()))
          .filter(m -> !m.getFecha().isBefore(start) && !m.getFecha().isAfter(end)).toList();
    }
    @Override public Movimiento save(Movimiento movimiento) { movimientos.add(movimiento); return movimiento; }
  }
}

package com.devsu.accountservice.application.usecase;

import com.devsu.accountservice.application.dto.EstadoCuentaResponse;
import com.devsu.accountservice.domain.exception.DomainException;
import com.devsu.accountservice.domain.model.Cuenta;
import com.devsu.accountservice.domain.model.Movimiento;
import com.devsu.accountservice.domain.repository.CuentaRepository;
import com.devsu.accountservice.domain.repository.MovimientoRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Application service that builds account statement reports. */
@RequiredArgsConstructor
@Service
public class ReporteUseCase {

  private final CuentaRepository cuentaRepository;
  private final MovimientoRepository movimientoRepository;

  /** Returns account statement rows for the provided customer and date range. */
  @Transactional(readOnly = true)
  public List<EstadoCuentaResponse> getEstadoCuenta(String fecha, String clienteId) {
    ReportDateRange range = parseRange(fecha);
    List<Cuenta> cuentas = cuentaRepository.findByClienteId(clienteId);

    if (cuentas.isEmpty()) {
      return List.of();
    }

    Map<String, Cuenta> cuentasPorNumero =
        cuentas.stream().collect(Collectors.toMap(Cuenta::getNumeroCuenta, Function.identity()));
    List<String> numerosCuenta = cuentas.stream().map(Cuenta::getNumeroCuenta).toList();

    return movimientoRepository
        .findByNumeroCuentaInAndFechaBetween(numerosCuenta, range.start(), range.end())
        .stream()
        .sorted(Comparator.comparing(Movimiento::getFecha))
        .map(
            movimiento ->
                toResponse(movimiento, cuentasPorNumero.get(movimiento.getNumeroCuenta())))
        .toList();
  }

  private EstadoCuentaResponse toResponse(Movimiento movimiento, Cuenta cuenta) {
    return new EstadoCuentaResponse(
        movimiento.getFecha(),
        cuenta.getClienteId(),
        cuenta.getNumeroCuenta(),
        cuenta.getTipoCuenta(),
        cuenta.getSaldoInicial(),
        cuenta.isEstado(),
        movimiento.getValor(),
        movimiento.getSaldo());
  }

  private ReportDateRange parseRange(String fecha) {
    if (fecha == null || fecha.isBlank()) {
      throw new DomainException("fecha es requerida");
    }

    String[] values = fecha.split(",", -1);
    if (values.length != 2) {
      throw new DomainException("fecha debe tener formato yyyy-MM-dd,yyyy-MM-dd");
    }

    try {
      LocalDate start = LocalDate.parse(values[0].trim());
      LocalDate end = LocalDate.parse(values[1].trim());
      if (end.isBefore(start)) {
        throw new DomainException("fecha final no puede ser menor que fecha inicial");
      }
      return new ReportDateRange(start.atStartOfDay(), end.atTime(LocalTime.MAX));
    } catch (DateTimeParseException exception) {
      throw new DomainException("fecha debe tener formato yyyy-MM-dd,yyyy-MM-dd");
    }
  }

  private record ReportDateRange(LocalDateTime start, LocalDateTime end) {}
}

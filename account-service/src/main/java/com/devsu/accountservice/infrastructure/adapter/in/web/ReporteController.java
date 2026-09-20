package com.devsu.accountservice.infrastructure.adapter.in.web;

import com.devsu.accountservice.application.port.in.ReporteInputPort;
import com.devsu.accountservice.infrastructure.adapter.in.web.dto.EstadoCuentaResponse;
import com.devsu.accountservice.infrastructure.adapter.in.web.mapper.ReporteWebMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for account statement reports. */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

  private final ReporteInputPort reporteInputPort;
  private final ReporteWebMapper reporteWebMapper;

  /** Returns account statement rows for a customer and date range. */
  @GetMapping
  ApiResponse<List<EstadoCuentaResponse>> getEstadoCuenta(
      @RequestParam String fecha, @RequestParam("cliente") String clienteId) {
    return ApiResponse.success("Reporte consultado exitosamente",
        reporteWebMapper.toResponses(reporteInputPort.getEstadoCuenta(fecha, clienteId)));
  }
}

package com.devsu.accountservice.application.usecase;

import com.devsu.accountservice.application.dto.CuentaPatchRequest;
import com.devsu.accountservice.application.dto.CuentaRequest;
import com.devsu.accountservice.application.dto.CuentaResponse;
import com.devsu.accountservice.application.mapper.CuentaMapper;
import com.devsu.accountservice.domain.exception.CuentaDuplicadaException;
import com.devsu.accountservice.domain.exception.CuentaNoEncontradaException;
import com.devsu.accountservice.domain.model.Cuenta;
import com.devsu.accountservice.domain.repository.CuentaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Application service that coordinates account use cases. */
@RequiredArgsConstructor
@Service
public class CuentaUseCase {

  private final CuentaRepository cuentaRepository;
  private final CuentaMapper cuentaMapper;

  /** Creates a new account. */
  @Transactional
  public CuentaResponse create(CuentaRequest request) {
    if (cuentaRepository.existsByNumeroCuenta(request.numeroCuenta())) {
      throw new CuentaDuplicadaException(request.numeroCuenta());
    }
    Cuenta cuenta = cuentaRepository.save(cuentaMapper.toDomain(request));
    return cuentaMapper.toResponse(cuenta);
  }

  /** Lists all accounts. */
  @Transactional(readOnly = true)
  public List<CuentaResponse> list() {
    return cuentaRepository.findAll().stream().map(cuentaMapper::toResponse).toList();
  }

  /** Returns one account by account number. */
  @Transactional(readOnly = true)
  public CuentaResponse get(String numeroCuenta) {
    return cuentaMapper.toResponse(findCuenta(numeroCuenta));
  }

  /** Replaces editable account data. */
  @Transactional
  public CuentaResponse replace(String numeroCuenta, CuentaRequest request) {
    Cuenta current = findCuenta(numeroCuenta);
    Cuenta updated = cuentaRepository.save(cuentaMapper.replace(current, request));
    return cuentaMapper.toResponse(updated);
  }

  /** Applies a partial update to editable account data. */
  @Transactional
  public CuentaResponse patch(String numeroCuenta, CuentaPatchRequest request) {
    Cuenta current = findCuenta(numeroCuenta);
    Cuenta updated = cuentaRepository.save(cuentaMapper.mergePatch(current, request));
    return cuentaMapper.toResponse(updated);
  }

  private Cuenta findCuenta(String numeroCuenta) {
    return cuentaRepository.findByNumeroCuenta(numeroCuenta)
        .orElseThrow(() -> new CuentaNoEncontradaException(numeroCuenta));
  }
}

package com.devsu.accountservice.infrastructure.adapter.out.persistence.repository;

import com.devsu.accountservice.infrastructure.adapter.out.persistence.entity.MovimientoEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data repository for movement entities. */
public interface SpringDataMovimientoRepository extends JpaRepository<MovimientoEntity, String> {

  /** Finds movement entities by account numbers and date range. */
  List<MovimientoEntity> findByNumeroCuentaInAndFechaBetweenOrderByFechaAsc(
      List<String> numerosCuenta, LocalDateTime start, LocalDateTime end);
}

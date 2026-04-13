package com.devsu.accountservice.infrastructure.persistence.repository;

import com.devsu.accountservice.infrastructure.persistence.entity.MovimientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data repository for movement entities. */
public interface SpringDataMovimientoRepository extends JpaRepository<MovimientoEntity, String> {}

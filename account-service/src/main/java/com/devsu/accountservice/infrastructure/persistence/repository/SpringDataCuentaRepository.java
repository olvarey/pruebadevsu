package com.devsu.accountservice.infrastructure.persistence.repository;

import com.devsu.accountservice.infrastructure.persistence.entity.CuentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data repository for account entities. */
public interface SpringDataCuentaRepository extends JpaRepository<CuentaEntity, String> {}

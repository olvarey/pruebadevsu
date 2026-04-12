package com.devsu.customerservice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataClienteRepository extends JpaRepository<ClienteEntity, String> {

    Optional<ClienteEntity> findByIdentificacion(String identificacion);
}

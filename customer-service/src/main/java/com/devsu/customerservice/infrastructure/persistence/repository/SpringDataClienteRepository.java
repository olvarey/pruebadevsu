package com.devsu.customerservice.infrastructure.persistence.repository;

import com.devsu.customerservice.infrastructure.persistence.entity.ClienteEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data repository for customer persistence entities. */
public interface SpringDataClienteRepository extends JpaRepository<ClienteEntity, String> {

  /** Finds a customer entity by its unique personal identification. */
  Optional<ClienteEntity> findByIdentificacion(String identificacion);
}

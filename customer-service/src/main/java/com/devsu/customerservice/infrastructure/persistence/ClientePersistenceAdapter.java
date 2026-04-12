package com.devsu.customerservice.infrastructure.persistence;

import com.devsu.customerservice.domain.model.Cliente;
import com.devsu.customerservice.domain.repository.ClienteRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ClientePersistenceAdapter implements ClienteRepository {

    private final SpringDataClienteRepository repository;

    public ClientePersistenceAdapter(SpringDataClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Cliente save(Cliente cliente) {
        return toDomain(repository.save(toEntity(cliente)));
    }

    @Override
    public Optional<Cliente> findByClienteId(String clienteId) {
        return repository.findById(clienteId).map(this::toDomain);
    }

    @Override
    public Optional<Cliente> findByIdentificacion(String identificacion) {
        return repository.findByIdentificacion(identificacion).map(this::toDomain);
    }

    @Override
    public List<Cliente> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByClienteId(String clienteId) {
        return repository.existsById(clienteId);
    }

    private ClienteEntity toEntity(Cliente cliente) {
        return new ClienteEntity(
                cliente.clienteId(),
                cliente.nombre(),
                cliente.genero(),
                cliente.edad(),
                cliente.identificacion(),
                cliente.direccion(),
                cliente.telefono(),
                cliente.contrasena(),
                cliente.estado()
        );
    }

    private Cliente toDomain(ClienteEntity entity) {
        return new Cliente(
                entity.getClienteId(),
                entity.getNombre(),
                entity.getGenero(),
                entity.getEdad(),
                entity.getIdentificacion(),
                entity.getDireccion(),
                entity.getTelefono(),
                entity.getContrasena(),
                entity.isEstado()
        );
    }
}

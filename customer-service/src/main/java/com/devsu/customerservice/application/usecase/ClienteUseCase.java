package com.devsu.customerservice.application.usecase;

import com.devsu.customerservice.application.dto.ClientePatchRequest;
import com.devsu.customerservice.application.dto.ClienteRequest;
import com.devsu.customerservice.application.dto.ClienteResponse;
import com.devsu.customerservice.application.mapper.ClienteMapper;
import com.devsu.customerservice.domain.exception.ClienteDuplicadoException;
import com.devsu.customerservice.domain.exception.ClienteNoEncontradoException;
import com.devsu.customerservice.domain.model.Cliente;
import com.devsu.customerservice.domain.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteUseCase {

    private final ClienteRepository clienteRepository;

    public ClienteUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public ClienteResponse create(ClienteRequest request) {
        ensureUniqueForCreate(request.clienteId(), request.identificacion());
        return ClienteMapper.toResponse(clienteRepository.save(ClienteMapper.toDomain(request)));
    }

    @Transactional(readOnly = true)
    public ClienteResponse get(String clienteId) {
        return ClienteMapper.toResponse(find(clienteId));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> list() {
        return clienteRepository.findAll().stream()
                .map(ClienteMapper::toResponse)
                .toList();
    }

    @Transactional
    public ClienteResponse replace(String clienteId, ClienteRequest request) {
        find(clienteId);
        if (!clienteId.equals(request.clienteId()) && clienteRepository.existsByClienteId(request.clienteId())) {
            throw new ClienteDuplicadoException("clienteId ya existe");
        }
        ensureIdentificationAvailable(request.identificacion(), clienteId);
        return ClienteMapper.toResponse(clienteRepository.save(ClienteMapper.toDomain(request)));
    }

    @Transactional
    public ClienteResponse patch(String clienteId, ClientePatchRequest request) {
        Cliente current = find(clienteId);
        String identificacion = valueOrCurrent(request.identificacion(), current.identificacion());
        ensureIdentificationAvailable(identificacion, clienteId);

        Cliente updated = new Cliente(
                current.clienteId(),
                valueOrCurrent(request.nombre(), current.nombre()),
                valueOrCurrent(request.genero(), current.genero()),
                request.edad() != null ? request.edad() : current.edad(),
                identificacion,
                valueOrCurrent(request.direccion(), current.direccion()),
                valueOrCurrent(request.telefono(), current.telefono()),
                valueOrCurrent(request.contrasena(), current.contrasena()),
                request.estado() != null ? request.estado() : current.estado()
        );
        return ClienteMapper.toResponse(clienteRepository.save(updated));
    }

    @Transactional
    public void delete(String clienteId) {
        clienteRepository.save(find(clienteId).desactivar());
    }

    private Cliente find(String clienteId) {
        return clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException(clienteId));
    }

    private void ensureUniqueForCreate(String clienteId, String identificacion) {
        if (clienteRepository.existsByClienteId(clienteId)) {
            throw new ClienteDuplicadoException("clienteId ya existe");
        }
        if (clienteRepository.findByIdentificacion(identificacion).isPresent()) {
            throw new ClienteDuplicadoException("identificacion ya existe");
        }
    }

    private void ensureIdentificationAvailable(String identificacion, String currentClienteId) {
        clienteRepository.findByIdentificacion(identificacion)
                .filter(existing -> !existing.clienteId().equals(currentClienteId))
                .ifPresent(existing -> {
                    throw new ClienteDuplicadoException("identificacion ya existe");
                });
    }

    private String valueOrCurrent(String value, String current) {
        return value != null ? value : current;
    }
}

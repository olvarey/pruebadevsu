package com.devsu.customerservice.application.mapper;

import com.devsu.customerservice.application.dto.ClienteRequest;
import com.devsu.customerservice.application.dto.ClienteResponse;
import com.devsu.customerservice.domain.model.Cliente;

public final class ClienteMapper {

    private ClienteMapper() {
    }

    public static Cliente toDomain(ClienteRequest request) {
        return new Cliente(
                request.clienteId(),
                request.nombre(),
                request.genero(),
                request.edad(),
                request.identificacion(),
                request.direccion(),
                request.telefono(),
                request.contrasena(),
                request.estado()
        );
    }

    public static ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.clienteId(),
                cliente.nombre(),
                cliente.genero(),
                cliente.edad(),
                cliente.identificacion(),
                cliente.direccion(),
                cliente.telefono(),
                cliente.estado()
        );
    }
}

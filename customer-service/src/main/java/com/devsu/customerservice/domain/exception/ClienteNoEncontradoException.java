package com.devsu.customerservice.domain.exception;

public class ClienteNoEncontradoException extends RuntimeException {

    public ClienteNoEncontradoException(String clienteId) {
        super("Cliente no encontrado: " + clienteId);
    }
}

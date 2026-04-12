package com.devsu.customerservice.domain.exception;

public class ClienteDuplicadoException extends RuntimeException {

    public ClienteDuplicadoException(String message) {
        super(message);
    }
}

package com.devsu.customerservice.infrastructure.web;

import com.devsu.customerservice.domain.exception.ClienteDuplicadoException;
import com.devsu.customerservice.domain.exception.ClienteNoEncontradoException;
import com.devsu.customerservice.domain.exception.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClienteNoEncontradoException.class)
    ResponseEntity<ApiError> notFound(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(HttpStatus.NOT_FOUND.value(), exception.getMessage()));
    }

    @ExceptionHandler(ClienteDuplicadoException.class)
    ResponseEntity<ApiError> conflict(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of(HttpStatus.CONFLICT.value(), exception.getMessage()));
    }

    @ExceptionHandler(DomainException.class)
    ResponseEntity<ApiError> badRequest(RuntimeException exception) {
        return ResponseEntity.badRequest()
                .body(ApiError.of(HttpStatus.BAD_REQUEST.value(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest()
                .body(new ApiError(Instant.now(), HttpStatus.BAD_REQUEST.value(), "Solicitud invalida", errors));
    }
}

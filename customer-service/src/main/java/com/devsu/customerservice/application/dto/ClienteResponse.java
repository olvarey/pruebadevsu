package com.devsu.customerservice.application.dto;

public record ClienteResponse(
        String clienteId,
        String nombre,
        String genero,
        Integer edad,
        String identificacion,
        String direccion,
        String telefono,
        boolean estado
) {
}

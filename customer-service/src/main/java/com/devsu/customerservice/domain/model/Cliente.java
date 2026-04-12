package com.devsu.customerservice.domain.model;

public class Cliente extends Persona {

    private final String clienteId;
    private final String contrasena;
    private final boolean estado;

    public Cliente(
            String clienteId,
            String nombre,
            String genero,
            Integer edad,
            String identificacion,
            String direccion,
            String telefono,
            String contrasena,
            boolean estado
    ) {
        super(nombre, genero, edad, identificacion, direccion, telefono);
        this.clienteId = requireText(clienteId, "clienteId");
        this.contrasena = requireText(contrasena, "contrasena");
        this.estado = estado;
    }

    public Cliente desactivar() {
        return withEstado(false);
    }

    public Cliente withEstado(boolean nuevoEstado) {
        return new Cliente(
                clienteId,
                nombre(),
                genero(),
                edad(),
                identificacion(),
                direccion(),
                telefono(),
                contrasena,
                nuevoEstado
        );
    }

    public String clienteId() {
        return clienteId;
    }

    public String contrasena() {
        return contrasena;
    }

    public boolean estado() {
        return estado;
    }
}

package com.devsu.customerservice.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
public class ClienteEntity extends PersonaEntity {

    @Id
    @Column(name = "cliente_id", nullable = false, length = 60)
    private String clienteId;

    @Column(nullable = false)
    private String contrasena;

    @Column(nullable = false)
    private boolean estado;

    protected ClienteEntity() {
    }

    public ClienteEntity(String clienteId, String nombre, String genero, Integer edad, String identificacion,
                         String direccion, String telefono, String contrasena, boolean estado) {
        super(nombre, genero, edad, identificacion, direccion, telefono);
        this.clienteId = clienteId;
        this.contrasena = contrasena;
        this.estado = estado;
    }

    public String getClienteId() {
        return clienteId;
    }

    public String getContrasena() {
        return contrasena;
    }

    public boolean isEstado() {
        return estado;
    }
}

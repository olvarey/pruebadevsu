package com.devsu.customerservice.infrastructure.web;

import com.devsu.customerservice.application.dto.ClientePatchRequest;
import com.devsu.customerservice.application.dto.ClienteRequest;
import com.devsu.customerservice.application.dto.ClienteResponse;
import com.devsu.customerservice.application.usecase.ClienteUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteUseCase clienteUseCase;

    public ClienteController(ClienteUseCase clienteUseCase) {
        this.clienteUseCase = clienteUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ClienteResponse create(@Valid @RequestBody ClienteRequest request) {
        return clienteUseCase.create(request);
    }

    @GetMapping
    List<ClienteResponse> list() {
        return clienteUseCase.list();
    }

    @GetMapping("/{clienteId}")
    ClienteResponse get(@PathVariable String clienteId) {
        return clienteUseCase.get(clienteId);
    }

    @PutMapping("/{clienteId}")
    ClienteResponse replace(@PathVariable String clienteId, @Valid @RequestBody ClienteRequest request) {
        return clienteUseCase.replace(clienteId, request);
    }

    @PatchMapping("/{clienteId}")
    ClienteResponse patch(@PathVariable String clienteId, @Valid @RequestBody ClientePatchRequest request) {
        return clienteUseCase.patch(clienteId, request);
    }

    @DeleteMapping("/{clienteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable String clienteId) {
        clienteUseCase.delete(clienteId);
    }
}

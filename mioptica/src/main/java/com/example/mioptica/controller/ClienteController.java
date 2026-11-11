package com.example.mioptica.controller;

import com.example.mioptica.dto.ClienteRequestDTO;
import com.example.mioptica.dto.ClienteResponseDTO;
import com.example.mioptica.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public List<ClienteResponseDTO> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public ClienteResponseDTO obtener(@PathVariable Integer id) {
        return clienteService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponseDTO crear(@Valid @RequestBody ClienteRequestDTO dto) {
        return clienteService.crear(dto);
    }

    @PutMapping("/{id}")
    public ClienteResponseDTO actualizar(@PathVariable Integer id,
                                         @Valid @RequestBody ClienteRequestDTO dto) {
        return clienteService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        clienteService.eliminar(id);
    }
}
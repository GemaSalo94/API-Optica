package com.example.mioptica.controller;

import com.example.mioptica.dto.ServicioRequestDTO;
import com.example.mioptica.dto.ServicioResponseDTO;
import com.example.mioptica.service.ServicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
public class ServicioController {

    private final ServicioService servicioService;

    @GetMapping
    public List<ServicioResponseDTO> listar() {
        return servicioService.listar();
    }

    @GetMapping("/{id}")
    public ServicioResponseDTO obtener(@PathVariable Integer id) {
        return servicioService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServicioResponseDTO crear(@Valid @RequestBody ServicioRequestDTO dto) {
        return servicioService.crear(dto);
    }

    @PutMapping("/{id}")
    public ServicioResponseDTO actualizar(@PathVariable Integer id,
                                          @Valid @RequestBody ServicioRequestDTO dto) {
        return servicioService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        servicioService.eliminar(id);
    }
}
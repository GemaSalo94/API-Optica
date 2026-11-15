package com.example.mioptica.controller;

import com.example.mioptica.dto.CitaRequestDTO;
import com.example.mioptica.dto.CitaResponseDTO;
import com.example.mioptica.dto.EstadoRequestDTO;
import com.example.mioptica.service.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    @GetMapping
    public List<CitaResponseDTO> listar(
            @RequestParam(required = false) Integer idCliente,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        if (idCliente != null) return citaService.listarPorCliente(idCliente);
        if (fecha != null) return citaService.listarPorDia(fecha);
        return citaService.listar();
    }

    @GetMapping("/disponibilidad")
    public List<String> disponibilidad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam Integer idServicio) {
        return citaService.disponibilidad(fecha, idServicio);
    }
    @GetMapping("/{id}")
    public CitaResponseDTO obtener(@PathVariable Integer id) {
        return citaService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CitaResponseDTO crear(@Valid @RequestBody CitaRequestDTO dto) {
        return citaService.crear(dto);
    }

    @PutMapping("/{id}")
    public CitaResponseDTO actualizar( @PathVariable Integer id,
                                       @Valid @RequestBody CitaRequestDTO dto) {
        return citaService.actualizar(id, dto);
    }

    @PatchMapping("/{id}/estado")
    public CitaResponseDTO cambiarEstado(@PathVariable Integer id,
                                         @Valid @RequestBody EstadoRequestDTO dto) {
        return citaService.cambiarEstado(id, dto.estado());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        citaService.eliminar(id);
    }
}
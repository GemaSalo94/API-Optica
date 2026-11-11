package com.example.mioptica.controller;

import com.example.mioptica.dto.TramoHorarioRequestDTO;
import com.example.mioptica.dto.TramoHorarioResponseDTO;
import com.example.mioptica.model.TramoHorario;
import com.example.mioptica.service.TramoHorarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tramos")
@RequiredArgsConstructor
public class TramoHorarioController {

    private final TramoHorarioService tramoService;

    @GetMapping
    public List<TramoHorarioResponseDTO> listar(
            @RequestParam(required = false) TramoHorario.DiaSemana dia) {
        return (dia == null) ? tramoService.listar() : tramoService.listarPorDia(dia);
    }

    @GetMapping("/{id}")
    public TramoHorarioResponseDTO obtener(@PathVariable Integer id) {
        return tramoService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TramoHorarioResponseDTO crear(@Valid @RequestBody TramoHorarioRequestDTO dto) {
        return tramoService.crear(dto);
    }

    @PutMapping("/{id}")
    public TramoHorarioResponseDTO actualizar(@PathVariable Integer id,
                                              @Valid @RequestBody TramoHorarioRequestDTO dto) {
        return tramoService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        tramoService.eliminar(id);
    }
}
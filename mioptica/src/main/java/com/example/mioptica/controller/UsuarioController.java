package com.example.mioptica.controller;

import com.example.mioptica.dto.UsuarioRequestDTO;
import com.example.mioptica.dto.UsuarioResponseDTO;
import com.example.mioptica.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioResponseDTO> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    public UsuarioResponseDTO obtener(@PathVariable Integer id) {
        return usuarioService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO crear(@Valid @RequestBody UsuarioRequestDTO dto) {
        return usuarioService.crear(dto);
    }

    @PutMapping("/{id}")
    public UsuarioResponseDTO actualizar(@PathVariable Integer id,
                                         @Valid @RequestBody UsuarioRequestDTO dto) {
        return usuarioService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        usuarioService.eliminar(id);
    }
}
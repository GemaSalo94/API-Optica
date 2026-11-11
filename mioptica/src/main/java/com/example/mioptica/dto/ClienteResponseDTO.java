package com.example.mioptica.dto;

public record ClienteResponseDTO(
        Integer id,
        String nombre,
        String apellidos,
        String telefono,
        Integer idUsuario
) {}
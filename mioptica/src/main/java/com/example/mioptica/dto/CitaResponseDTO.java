package com.example.mioptica.dto;

import java.time.LocalDateTime;

public record CitaResponseDTO(
        Integer id,
        Integer idCliente,
        String nombreCliente,
        Integer idServicio,
        String nombreServicio,
        LocalDateTime fechaHora,
        LocalDateTime horaFin,
        String estado
) {}
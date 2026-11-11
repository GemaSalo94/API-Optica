package com.example.mioptica.dto;

import com.example.mioptica.model.TramoHorario.DiaSemana;
import java.time.LocalTime;

public record TramoHorarioResponseDTO(
        Integer id,
        DiaSemana diaSemana,
        LocalTime horaInicio,
        LocalTime horaFin
) {}
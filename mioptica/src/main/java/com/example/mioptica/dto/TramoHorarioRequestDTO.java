package com.example.mioptica.dto;

import com.example.mioptica.model.TramoHorario.DiaSemana;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record TramoHorarioRequestDTO(
        @NotNull(message = "El día de la semana es obligatorio")
        DiaSemana diaSemana,

        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime horaInicio,

        @NotNull(message = "La hora de fin es obligatoria")
        LocalTime horaFin
) {}
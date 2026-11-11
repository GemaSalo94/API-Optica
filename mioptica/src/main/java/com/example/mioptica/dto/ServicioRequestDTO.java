package com.example.mioptica.dto;

import jakarta.validation.constraints.*;

public record ServicioRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @NotNull(message = "La duración es obligatoria")
        @Positive(message = "La duración debe ser mayor que 0")
        @Max(value = 120, message = "La duración no puede superar 120 minutos")
        Integer duracionMin
) {}
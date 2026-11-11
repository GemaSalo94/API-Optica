package com.example.mioptica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EstadoRequestDTO(
        @NotBlank(message = "El estado es obligatorio")
        @Pattern(regexp = "PENDIENTE|CONFIRMADA|CANCELADA|COMPLETADA", message = "Estado no válido")
        String estado
) {}
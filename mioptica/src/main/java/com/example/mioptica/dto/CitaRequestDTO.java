package com.example.mioptica.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record CitaRequestDTO(
        @NotNull(message = "El cliente es obligatorio")
        Integer idCliente,
        @NotNull(message = "El servicio es obligatorio")
        Integer idServicio,
        @NotNull(message = "La fecha y hora son obligatorias")
        @Future(message = "La cita debe ser en el futuro")
        LocalDateTime fechaHora,
        String estado
) {}
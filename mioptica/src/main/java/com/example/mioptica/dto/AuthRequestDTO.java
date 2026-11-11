package com.example.mioptica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email no válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}
package com.example.mioptica.dto;

import jakarta.validation.constraints.*;

public record UsuarioRequestDTO(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email no válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
        String password,

        @NotBlank(message = "El rol es obligatorio")
        @Pattern(regexp = "ADMIN|CLIENTE", message = "Rol no válido")
        String rol
) {}
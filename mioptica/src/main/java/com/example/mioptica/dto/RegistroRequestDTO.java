package com.example.mioptica.dto;

import jakarta.validation.constraints.*;

public record RegistroRequestDTO(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email no válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 10, message = "La contraseña debe tener entre 8 y 10 caracteres")
        String password,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50)
        String nombre,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100)
        String apellidos,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^(\\+34)?[0-9]{9}$", message = "Teléfono no válido")
        String telefono
) {}
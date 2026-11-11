package com.example.mioptica.dto;

import jakarta.validation.constraints.*;

public record ClienteRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50) String nombre,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100) String apellidos,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^(\\+34)?[0-9]{9}$", message = "Teléfono no válido")
        String telefono,

        @Email(message = "Formato de email no válido")
        String email,          // solo al crear cliente nuevo

        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
        String password,       // solo al crear cliente nuevo

        Integer idUsuario      // alternativa: vincular a un usuario ya existente
) {}
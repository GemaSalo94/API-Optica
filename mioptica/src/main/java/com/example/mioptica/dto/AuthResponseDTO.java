package com.example.mioptica.dto;

public record AuthResponseDTO(
        String token,
        String email,
        String rol
) {}
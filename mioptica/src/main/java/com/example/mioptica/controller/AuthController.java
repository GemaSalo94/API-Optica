package com.example.mioptica.controller;

import com.example.mioptica.dto.AuthRequestDTO;
import com.example.mioptica.dto.AuthResponseDTO;
import com.example.mioptica.dto.RegistroRequestDTO;
import com.example.mioptica.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody AuthRequestDTO dto) {
        return authService.login(dto);
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDTO registro(@Valid @RequestBody RegistroRequestDTO dto) {
        return authService.registro(dto);
    }
}
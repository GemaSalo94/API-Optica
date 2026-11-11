package com.example.mioptica.service;

import com.example.mioptica.dto.*;
import com.example.mioptica.model.Usuario;
import com.example.mioptica.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final ClienteService clienteService;

    public AuthResponseDTO login(AuthRequestDTO dto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));
        Usuario usuario = usuarioRepository.findByEmail(dto.email()).orElseThrow();
        return new AuthResponseDTO(jwtService.generateToken(auth), usuario.getEmail(), usuario.getRol());
    }

    public AuthResponseDTO registro(RegistroRequestDTO dto) {
        // reutiliza el mismo camino que el admin: crea usuario CLIENTE + cliente
        clienteService.crear(new ClienteRequestDTO(
                dto.nombre(), dto.apellidos(), dto.telefono(),
                dto.email(), dto.password(), null));

        return login(new AuthRequestDTO(dto.email(), dto.password()));
    }
}
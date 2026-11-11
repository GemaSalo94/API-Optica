package com.example.mioptica.service;

import com.example.mioptica.dto.UsuarioRequestDTO;
import com.example.mioptica.dto.UsuarioResponseDTO;
import com.example.mioptica.exceptions.RecursoNoEncontradoException;
import com.example.mioptica.model.Usuario;
import com.example.mioptica.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;   // añadir

    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UsuarioResponseDTO obtener(Integer id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.email());
        usuario.setPassword(passwordEncoder.encode(dto.password()));
        usuario.setRol(dto.rol());
        return toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO actualizar(Integer id, UsuarioRequestDTO dto) {
        Usuario usuario = buscar(id);
        usuario.setEmail(dto.email());
        usuario.setPassword(passwordEncoder.encode(dto.password()));
        usuario.setRol(dto.rol());
        return toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void eliminar(Integer id) {
        usuarioRepository.delete(buscar(id));
    }

    private Usuario buscar(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id));
    }

    private UsuarioResponseDTO toResponse(Usuario u) {
        return new UsuarioResponseDTO(u.getId(), u.getEmail(), u.getRol());
    }
}

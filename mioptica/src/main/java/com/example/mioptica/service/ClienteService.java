package com.example.mioptica.service;

import com.example.mioptica.dto.ClienteRequestDTO;
import com.example.mioptica.dto.ClienteResponseDTO;
import com.example.mioptica.exceptions.RecursoNoEncontradoException;
import com.example.mioptica.model.Cliente;
import com.example.mioptica.model.Usuario;
import com.example.mioptica.repository.ClienteRepository;
import com.example.mioptica.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClienteService {

    private static final String ROL_CLIENTE = "CLIENTE";

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<ClienteResponseDTO> listar() {
        return clienteRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ClienteResponseDTO obtener(Integer id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public ClienteResponseDTO crear(ClienteRequestDTO dto) {
        Cliente cliente = new Cliente();
        aplicarDatos(cliente, dto);
        return toResponse(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteResponseDTO actualizar(Integer id, ClienteRequestDTO dto) {
        Cliente cliente = buscar(id);
        aplicarDatos(cliente, dto);
        return toResponse(clienteRepository.save(cliente));
    }

    @Transactional
    public void eliminar(Integer id) {
        clienteRepository.delete(buscar(id));
    }

    private void aplicarDatos(Cliente cliente, ClienteRequestDTO dto) {
        cliente.setNombre(dto.nombre());
        cliente.setApellidos(dto.apellidos());
        cliente.setTelefono(dto.telefono());
        cliente.setUsuario(resolverUsuario(cliente, dto));
    }

    /** Decide si vincular, actualizar o crear la cuenta del cliente. */
    private Usuario resolverUsuario(Cliente cliente, ClienteRequestDTO dto) {
        if (dto.idUsuario() != null) {
            return vincularUsuarioExistente(cliente, dto.idUsuario());
        }

        boolean pideCuenta = dto.email() != null && !dto.email().isBlank();

        if (cliente.getUsuario() != null) {
            if (pideCuenta) {
                actualizarCuenta(cliente.getUsuario(), dto);
            }
            return cliente.getUsuario();   // conserva la cuenta actual
        }

        return pideCuenta ? crearCuenta(dto) : null;
    }

    private Usuario vincularUsuarioExistente(Cliente cliente, Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + idUsuario));

        clienteRepository.findByUsuarioId(idUsuario).ifPresent(otro -> {
            boolean esElMismo = cliente.getId() != null && cliente.getId().equals(otro.getId());
            if (!esElMismo) {
                throw new IllegalArgumentException("Ese usuario ya está vinculado al cliente " + otro.getId());
            }
        });
        return usuario;
    }

    private Usuario crearCuenta(ClienteRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }
        validarPassword(dto.password());

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.email());
        usuario.setPassword(passwordEncoder.encode(dto.password()));
        usuario.setRol(ROL_CLIENTE);
        return usuarioRepository.save(usuario);
    }

    private void actualizarCuenta(Usuario usuario, ClienteRequestDTO dto) {
        boolean cambiaEmail = !usuario.getEmail().equalsIgnoreCase(dto.email());
        if (cambiaEmail && usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }
        usuario.setEmail(dto.email());

        // La contraseña solo se cambia si la envían
        if (dto.password() != null && !dto.password().isBlank()) {
            validarPassword(dto.password());
            usuario.setPassword(passwordEncoder.encode(dto.password()));
        }
        usuarioRepository.save(usuario);
    }

    private void validarPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("La contraseña es obligatoria (mínimo 8 caracteres)");
        }
    }

    private Cliente buscar(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado: " + id));
    }

    private ClienteResponseDTO toResponse(Cliente c) {
        return new ClienteResponseDTO(
                c.getId(),
                c.getNombre(),
                c.getApellidos(),
                c.getTelefono(),
                c.getUsuario() != null ? c.getUsuario().getId() : null
        );
    }
}
package com.example.mioptica.repository;

import com.example.mioptica.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByUsuarioId(Integer idUsuario);
    Optional<Cliente> findByTelefono(String telefono);
    Optional<Cliente> findByUsuarioEmail(String email);
}
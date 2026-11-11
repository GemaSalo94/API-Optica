package com.example.mioptica.repository;

import com.example.mioptica.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioRepository extends JpaRepository<Servicio, Integer> {

    boolean existsByNombreIgnoreCase(String nombre);
}
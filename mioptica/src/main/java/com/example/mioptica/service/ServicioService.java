package com.example.mioptica.service;

import com.example.mioptica.dto.ServicioRequestDTO;
import com.example.mioptica.dto.ServicioResponseDTO;
import com.example.mioptica.exceptions.RecursoNoEncontradoException;
import com.example.mioptica.model.Servicio;
import com.example.mioptica.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServicioService {

    private final ServicioRepository servicioRepository;

    public List<ServicioResponseDTO> listar() {
        return servicioRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ServicioResponseDTO obtener(Integer id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public ServicioResponseDTO crear(ServicioRequestDTO dto) {
        if (servicioRepository.existsByNombreIgnoreCase(dto.nombre())) {
            throw new IllegalArgumentException("Ya existe un servicio con ese nombre");
        }
        Servicio servicio = new Servicio();
        servicio.setNombre(dto.nombre());
        servicio.setDuracionMin(dto.duracionMin());
        return toResponse(servicioRepository.save(servicio));
    }

    @Transactional
    public ServicioResponseDTO actualizar(Integer id, ServicioRequestDTO dto) {
        Servicio servicio = buscar(id);
        servicio.setNombre(dto.nombre());
        servicio.setDuracionMin(dto.duracionMin());
        return toResponse(servicioRepository.save(servicio));
    }

    @Transactional
    public void eliminar(Integer id) {
        servicioRepository.delete(buscar(id));
    }

    private Servicio buscar(Integer id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Servicio no encontrado: " + id));
    }

    private ServicioResponseDTO toResponse(Servicio s) {
        return new ServicioResponseDTO(s.getId(), s.getNombre(), s.getDuracionMin());
    }
}
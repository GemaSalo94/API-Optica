package com.example.mioptica.service;

import com.example.mioptica.dto.TramoHorarioRequestDTO;
import com.example.mioptica.dto.TramoHorarioResponseDTO;
import com.example.mioptica.exceptions.RecursoNoEncontradoException;
import com.example.mioptica.model.TramoHorario;
import com.example.mioptica.repository.TramoHorarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TramoHorarioService {

    private final TramoHorarioRepository tramoRepository;

    public List<TramoHorarioResponseDTO> listar() {
        return tramoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<TramoHorarioResponseDTO> listarPorDia(TramoHorario.DiaSemana dia) {
        return tramoRepository.findByDiaSemanaOrderByHoraInicioAsc(dia)
                .stream().map(this::toResponse).toList();
    }

    public TramoHorarioResponseDTO obtener(Integer id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public TramoHorarioResponseDTO crear(TramoHorarioRequestDTO dto) {
        validar(dto);
        if (haySolape(dto, null)) {
            throw new IllegalArgumentException("El tramo se solapa con otro existente ese día");
        }
        TramoHorario tramo = new TramoHorario();
        aplicarDatos(tramo, dto);
        return toResponse(tramoRepository.save(tramo));
    }

    @Transactional
    public TramoHorarioResponseDTO actualizar(Integer id, TramoHorarioRequestDTO dto) {
        validar(dto);
        TramoHorario tramo = buscar(id);
        if (haySolape(dto, id)) {
            throw new IllegalArgumentException("El tramo se solapa con otro existente ese día");
        }
        aplicarDatos(tramo, dto);
        return toResponse(tramoRepository.save(tramo));
    }

    @Transactional
    public void eliminar(Integer id) {
        tramoRepository.delete(buscar(id));
    }

    private void validar(TramoHorarioRequestDTO dto) {
        if (dto.diaSemana() == null || dto.horaInicio() == null || dto.horaFin() == null) {
            throw new IllegalArgumentException("Día, hora inicio y hora fin son obligatorios");
        }
        if (!dto.horaFin().isAfter(dto.horaInicio())) {
            throw new IllegalArgumentException("La hora fin debe ser posterior a la hora inicio");
        }
    }

    private boolean haySolape(TramoHorarioRequestDTO dto, Integer idPropio) {
        // Aproximación: el repo detecta solape; al actualizar, excluye el propio tramo
        // si el repositorio no lo hace, comparamos en memoria.
        return tramoRepository.findByDiaSemanaOrderByHoraInicioAsc(dto.diaSemana()).stream()
                .filter(t -> idPropio == null || !t.getId().equals(idPropio))
                .anyMatch(t -> dto.horaInicio().isBefore(t.getHoraFin())
                        && dto.horaFin().isAfter(t.getHoraInicio()));
    }

    private void aplicarDatos(TramoHorario t, TramoHorarioRequestDTO dto) {
        t.setDiaSemana(dto.diaSemana());
        t.setHoraInicio(dto.horaInicio());
        t.setHoraFin(dto.horaFin());
    }

    private TramoHorario buscar(Integer id) {
        return tramoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tramo no encontrado: " + id));
    }

    private TramoHorarioResponseDTO toResponse(TramoHorario t) {
        return new TramoHorarioResponseDTO(t.getId(), t.getDiaSemana(), t.getHoraInicio(), t.getHoraFin());
    }
}
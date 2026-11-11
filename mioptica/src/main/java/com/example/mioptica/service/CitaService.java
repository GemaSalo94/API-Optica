package com.example.mioptica.service;

import com.example.mioptica.dto.CitaRequestDTO;
import com.example.mioptica.dto.CitaResponseDTO;
import com.example.mioptica.exceptions.RecursoNoEncontradoException;
import com.example.mioptica.model.*;
import com.example.mioptica.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CitaService {

    private static final Set<String> ESTADOS_VALIDOS =
            Set.of("PENDIENTE", "CONFIRMADA", "CANCELADA", "COMPLETADA");
    private static final String ESTADO_INICIAL = "PENDIENTE";

    private final CitaRepository citaRepository;
    private final ClienteRepository clienteRepository;
    private final ServicioRepository servicioRepository;

    public List<CitaResponseDTO> listar() {
        return citaRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<CitaResponseDTO> listarPorCliente(Integer idCliente) {
        return citaRepository.findByClienteId(idCliente).stream().map(this::toResponse).toList();
    }

    public List<CitaResponseDTO> listarPorDia(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        return citaRepository.findByFechaHoraBetweenOrderByFechaHoraAsc(inicio, fin)
                .stream().map(this::toResponse).toList();
    }

    public CitaResponseDTO obtener(Integer id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public CitaResponseDTO crear(CitaRequestDTO dto) {
        Cliente cliente = buscarCliente(dto.idCliente());
        Servicio servicio = buscarServicio(dto.idServicio());
        validarFecha(dto.fechaHora());
        String estado = dto.estado() == null ? ESTADO_INICIAL : dto.estado().toUpperCase();
        validarEstado(estado);
        comprobarSolape(dto.fechaHora(), servicio.getDuracionMin(), null);

        Cita cita = new Cita();
        cita.setCliente(cliente);
        cita.setServicio(servicio);
        cita.setFechaHora(dto.fechaHora());
        cita.setEstado(estado);
        return toResponse(citaRepository.save(cita));
    }

    @Transactional
    public CitaResponseDTO actualizar(Integer id, CitaRequestDTO dto) {
        Cita cita = buscar(id);
        Cliente cliente = buscarCliente(dto.idCliente());
        Servicio servicio = buscarServicio(dto.idServicio());
        validarFecha(dto.fechaHora());
        String estado = dto.estado() == null ? cita.getEstado() : dto.estado().toUpperCase();
        validarEstado(estado);
        comprobarSolape(dto.fechaHora(), servicio.getDuracionMin(), id);

        cita.setCliente(cliente);
        cita.setServicio(servicio);
        cita.setFechaHora(dto.fechaHora());
        cita.setEstado(estado);
        return toResponse(citaRepository.save(cita));
    }

    @Transactional
    public CitaResponseDTO cambiarEstado(Integer id, String nuevoEstado) {
        Cita cita = buscar(id);
        String estado = nuevoEstado.toUpperCase();
        validarEstado(estado);
        cita.setEstado(estado);
        return toResponse(citaRepository.save(cita));
    }

    @Transactional
    public void eliminar(Integer id) {
        citaRepository.delete(buscar(id));
    }

    private void comprobarSolape(LocalDateTime inicio, Integer duracionMin, Integer idPropio) {
        LocalDateTime fin = inicio.plusMinutes(duracionMin);
        boolean solapa = (idPropio == null)
                ? citaRepository.existeSolape(inicio, fin)
                : citaRepository.existeSolapeExcluyendo(inicio, fin, idPropio);
        if (solapa) {
            throw new IllegalArgumentException("Ya existe una cita que se solapa en ese horario");
        }
    }

    private void validarFecha(LocalDateTime fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha y hora son obligatorias");
        }
        if (fecha.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se puede agendar una cita en el pasado");
        }
    }

    private void validarEstado(String estado) {
        if (!ESTADOS_VALIDOS.contains(estado)) {
            throw new IllegalArgumentException("Estado no válido: " + estado);
        }
    }

    private Cliente buscarCliente(Integer id) {
        if (id == null) throw new IllegalArgumentException("idCliente es obligatorio");
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado: " + id));
    }

    private Servicio buscarServicio(Integer id) {
        if (id == null) throw new IllegalArgumentException("idServicio es obligatorio");
        return servicioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Servicio no encontrado: " + id));
    }

    private Cita buscar(Integer id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita no encontrada: " + id));
    }

    private CitaResponseDTO toResponse(Cita c) {
        return new CitaResponseDTO(
                c.getId(),
                c.getCliente().getId(),
                c.getCliente().getNombre() + " " + c.getCliente().getApellidos(),
                c.getServicio().getId(),
                c.getServicio().getNombre(),
                c.getFechaHora(),
                c.getFechaHora().plusMinutes(c.getServicio().getDuracionMin()),
                c.getEstado()
        );
    }
}
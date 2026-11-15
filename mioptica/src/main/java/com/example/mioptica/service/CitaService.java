package com.example.mioptica.service;

import com.example.mioptica.dto.CitaRequestDTO;
import com.example.mioptica.dto.CitaResponseDTO;
import com.example.mioptica.exceptions.RecursoNoEncontradoException;
import com.example.mioptica.model.Cliente;
import com.example.mioptica.model.Cita;
import com.example.mioptica.model.Servicio;
import com.example.mioptica.model.TramoHorario;
import com.example.mioptica.model.TramoHorario.DiaSemana;
import com.example.mioptica.repository.CitaRepository;
import com.example.mioptica.repository.ClienteRepository;
import com.example.mioptica.repository.ServicioRepository;
import com.example.mioptica.repository.TramoHorarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CitaService {

    private static final Set<String> ESTADOS_VALIDOS =
            Set.of("PENDIENTE", "CONFIRMADA", "CANCELADA", "COMPLETADA");
    private static final String ESTADO_INICIAL = "PENDIENTE";
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final int PASO_MINUTOS = 15;

    private final CitaRepository citaRepository;
    private final ClienteRepository clienteRepository;
    private final ServicioRepository servicioRepository;
    private final TramoHorarioRepository tramoRepository;

    // ---------- Consultas ----------

    public List<CitaResponseDTO> listar() {
        if (esRolCliente()) {
            Cliente propio = clienteAutenticado();
            return citaRepository.findByClienteId(propio.getId())
                    .stream().map(this::toResponse).toList();
        }
        return citaRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<CitaResponseDTO> listarPorCliente(Integer idCliente) {
        Integer id = esRolCliente() ? clienteAutenticado().getId() : idCliente;
        return citaRepository.findByClienteId(id).stream().map(this::toResponse).toList();
    }

    public List<CitaResponseDTO> listarPorDia(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        return citaRepository.findByFechaHoraBetweenOrderByFechaHoraAsc(inicio, fin)
                .stream().map(this::toResponse).toList();
    }

    public CitaResponseDTO obtener(Integer id) {
        Cita cita = buscar(id);
        verificarPropiedad(cita);
        return toResponse(cita);
    }

    public List<String> disponibilidad(LocalDate fecha, Integer idServicio) {
        Servicio servicio = buscarServicio(idServicio);
        DiaSemana dia = aDiaSemana(fecha.getDayOfWeek());
        List<TramoHorario> tramos = tramoRepository.findByDiaSemanaOrderByHoraInicioAsc(dia);

        List<String> huecos = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();

        for (TramoHorario tramo : tramos) {
            LocalDateTime slot = fecha.atTime(tramo.getHoraInicio());
            LocalDateTime finTramo = fecha.atTime(tramo.getHoraFin());

            while (!slot.plusMinutes(servicio.getDuracionMin()).isAfter(finTramo)) {
                LocalDateTime finSlot = slot.plusMinutes(servicio.getDuracionMin());

                boolean futuro = slot.isAfter(ahora);
                boolean libre = !citaRepository.existeSolape(slot, finSlot);

                if (futuro && libre) {
                    huecos.add(slot.toLocalTime().format(FORMATO_HORA));
                }
                slot = slot.plusMinutes(PASO_MINUTOS);
            }
        }
        return huecos;
    }

    // ---------- Escritura ----------

    @Transactional
    public CitaResponseDTO crear(CitaRequestDTO dto) {
        Cliente cliente = resolverCliente(dto.idCliente());
        Servicio servicio = buscarServicio(dto.idServicio());
        validarFecha(dto.fechaHora());

        String estado = esRolCliente()
                ? ESTADO_INICIAL
                : (dto.estado() == null ? ESTADO_INICIAL : dto.estado().toUpperCase());
        validarEstado(estado);

        validarHorarioApertura(dto.fechaHora(), servicio.getDuracionMin());
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
        verificarPropiedad(cita);

        Cliente cliente = resolverCliente(dto.idCliente());
        Servicio servicio = buscarServicio(dto.idServicio());
        validarFecha(dto.fechaHora());

        String estado = esRolCliente()
                ? cita.getEstado()
                : (dto.estado() == null ? cita.getEstado() : dto.estado().toUpperCase());
        validarEstado(estado);

        validarHorarioApertura(dto.fechaHora(), servicio.getDuracionMin());
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

        if (esRolCliente()) {
            Cliente propio = clienteAutenticado();
            if (!cita.getCliente().getId().equals(propio.getId())) {
                throw new AccessDeniedException("No puedes modificar una cita ajena");
            }
            if (!"PENDIENTE".equals(cita.getEstado()) || !"CANCELADA".equals(estado)) {
                throw new IllegalArgumentException("Solo puedes cancelar tus citas pendientes");
            }
        }

        cita.setEstado(estado);
        return toResponse(citaRepository.save(cita));
    }

    @Transactional
    public void eliminar(Integer id) {
        citaRepository.delete(buscar(id));
    }

    // ---------- Seguridad / rol ----------

    private boolean esRolCliente() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"));
    }

    private Cliente clienteAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return clienteRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe cliente para el usuario autenticado"));
    }

    private Cliente resolverCliente(Integer idCliente) {
        return esRolCliente() ? clienteAutenticado() : buscarCliente(idCliente);
    }

    private void verificarPropiedad(Cita cita) {
        if (esRolCliente() && !cita.getCliente().getId().equals(clienteAutenticado().getId())) {
            throw new AccessDeniedException("No puedes acceder a una cita ajena");
        }
    }

    // ---------- Validaciones ----------

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

    private void validarHorarioApertura(LocalDateTime inicio, int duracionMin) {
        LocalDateTime fin = inicio.plusMinutes(duracionMin);
        DiaSemana dia = aDiaSemana(inicio.getDayOfWeek());

        boolean dentro = tramoRepository.findByDiaSemanaOrderByHoraInicioAsc(dia).stream()
                .anyMatch(t -> !inicio.toLocalTime().isBefore(t.getHoraInicio())
                        && !fin.toLocalTime().isAfter(t.getHoraFin()));

        if (!dentro) {
            throw new IllegalArgumentException(
                    "La cita está fuera del horario de apertura del " + dia);
        }
    }

    private DiaSemana aDiaSemana(DayOfWeek d) {
        return switch (d) {
            case MONDAY -> DiaSemana.LUNES;
            case TUESDAY -> DiaSemana.MARTES;
            case WEDNESDAY -> DiaSemana.MIERCOLES;
            case THURSDAY -> DiaSemana.JUEVES;
            case FRIDAY -> DiaSemana.VIERNES;
            case SATURDAY -> DiaSemana.SABADO;
            case SUNDAY -> DiaSemana.DOMINGO;
        };
    }

    // ---------- Búsquedas ----------

    private Cliente buscarCliente(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("idCliente es obligatorio");
        }
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado: " + id));
    }

    private Servicio buscarServicio(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("idServicio es obligatorio");
        }
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
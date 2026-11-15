package com.example.mioptica.config;

import com.example.mioptica.model.Servicio;
import com.example.mioptica.model.TramoHorario;
import com.example.mioptica.model.TramoHorario.DiaSemana;
import com.example.mioptica.model.Usuario;
import com.example.mioptica.repository.ServicioRepository;
import com.example.mioptica.repository.TramoHorarioRepository;
import com.example.mioptica.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.mioptica.model.Cliente;
import com.example.mioptica.repository.ClienteRepository;

import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;
    private final TramoHorarioRepository tramoRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        crearAdmin();
        crearCliente();
        crearServicios();
        crearTramos();
    }

    private void crearAdmin() {
        if (usuarioRepository.existsByEmail("admin@mioptica.com")) {
            return;
        }
        Usuario admin = new Usuario();
        admin.setEmail("admin@mioptica.com");
        admin.setPassword(passwordEncoder.encode("admin1234"));
        admin.setRol("ADMIN");
        usuarioRepository.save(admin);
        System.out.println("DataInitializer: usuario ADMIN creado");
    }
    private void crearCliente() {
        if (usuarioRepository.existsByEmail("PeterParker@mioptica.com")) {
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setEmail("PeterParker@mioptica.com");
        usuario.setPassword(passwordEncoder.encode("cliente1234"));
        usuario.setRol("CLIENTE");
        usuarioRepository.save(usuario);

        Cliente cliente = new Cliente();
        cliente.setNombre("Peter");
        cliente.setApellidos("Parker");
        cliente.setTelefono("639544222");
        cliente.setUsuario(usuario);
        clienteRepository.save(cliente);

        System.out.println("DataInitializer: cliente de prueba creado");
    }

    private void crearServicios() {
        if (servicioRepository.count() > 0) {
            return;
        }
        servicioRepository.saveAll(List.of(
                servicio("Consulta optométrica", 45),
                servicio("Revisión contactología", 30),
                servicio("Revisión de control periódica", 25)
        ));
        System.out.println("DataInitializer: servicios de prueba creados");
    }

    private Servicio servicio(String nombre, int duracionMin) {
        Servicio s = new Servicio();
        s.setNombre(nombre);
        s.setDuracionMin(duracionMin);
        return s;
    }

    private void crearTramos() {
        if (tramoRepository.count() > 0) {
            return;
        }
        List<DiaSemana> laborables = List.of(
                DiaSemana.LUNES, DiaSemana.MARTES, DiaSemana.MIERCOLES,
                DiaSemana.JUEVES, DiaSemana.VIERNES
        );
        for (DiaSemana dia : laborables) {
            tramoRepository.save(tramo(dia, LocalTime.of(10, 0), LocalTime.of(13, 30)));
            tramoRepository.save(tramo(dia, LocalTime.of(17, 0), LocalTime.of(20, 30)));
        }
        System.out.println("DataInitializer: tramos horarios creados");
    }

    private TramoHorario tramo(DiaSemana dia, LocalTime inicio, LocalTime fin) {
        TramoHorario t = new TramoHorario();
        t.setDiaSemana(dia);
        t.setHoraInicio(inicio);
        t.setHoraFin(fin);
        return t;
    }
}
package com.example.mioptica.repository;

import com.example.mioptica.model.TramoHorario;
import com.example.mioptica.model.TramoHorario.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface TramoHorarioRepository extends JpaRepository<TramoHorario, Integer> {

    List<TramoHorario> findByDiaSemanaOrderByHoraInicioAsc(DiaSemana diaSemana);
    boolean existsByDiaSemanaAndHoraInicioLessThanAndHoraFinGreaterThan(
            DiaSemana diaSemana, LocalTime horaFin, LocalTime horaInicio);
}
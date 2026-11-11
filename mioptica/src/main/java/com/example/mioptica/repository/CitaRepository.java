package com.example.mioptica.repository;

import com.example.mioptica.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Integer> {

    List<Cita> findByClienteId(Integer idCliente);

    List<Cita> findByFechaHoraBetweenOrderByFechaHoraAsc(LocalDateTime inicio, LocalDateTime fin);

    @Query("""
        SELECT COUNT(c) > 0 FROM Cita c
        WHERE c.estado <> 'CANCELADA'
          AND c.fechaHora < :fin
          AND timestampadd(minute, c.servicio.duracionMin, c.fechaHora) > :inicio
        """)
    boolean existeSolape(@Param("inicio") LocalDateTime inicio,
                         @Param("fin") LocalDateTime fin);

    @Query("""
        SELECT COUNT(c) > 0 FROM Cita c
        WHERE c.estado <> 'CANCELADA'
          AND c.id <> :idCita
          AND c.fechaHora < :fin
          AND timestampadd(minute, c.servicio.duracionMin, c.fechaHora) > :inicio
        """)
    boolean existeSolapeExcluyendo(@Param("inicio") LocalDateTime inicio,
                                   @Param("fin") LocalDateTime fin,
                                   @Param("idCita") Integer idCita);
}
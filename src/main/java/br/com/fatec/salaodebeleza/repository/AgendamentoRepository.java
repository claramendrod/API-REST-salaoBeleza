package br.com.fatec.salaodebeleza.repository;

import br.com.fatec.salaodebeleza.model.Agendamento;
import br.com.fatec.salaodebeleza.model.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByStatusOrderByDataHoraInicioAsc(StatusAgendamento status);

    List<Agendamento> findByDataHoraInicioBetweenOrderByDataHoraInicioAsc(LocalDateTime inicio, LocalDateTime fim);

    List<Agendamento> findByClienteIdOrderByDataHoraInicioDesc(Long clienteId);

    List<Agendamento> findByStatusAndDataHoraInicioLessThanAndIdNot(
            StatusAgendamento status, LocalDateTime limite, Long idIgnorado);
}

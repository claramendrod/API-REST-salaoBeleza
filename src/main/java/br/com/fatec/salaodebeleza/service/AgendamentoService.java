package br.com.fatec.salaodebeleza.service;

import br.com.fatec.salaodebeleza.dto.AgendamentoRequest;
import br.com.fatec.salaodebeleza.model.*;
import br.com.fatec.salaodebeleza.repository.AgendamentoRepository;
import br.com.fatec.salaodebeleza.repository.ClienteRepository;
import br.com.fatec.salaodebeleza.repository.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AgendamentoService {
    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository clienteRepository;
    private final ServicoRepository servicoRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
                              ClienteRepository clienteRepository,
                              ServicoRepository servicoRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.clienteRepository = clienteRepository;
        this.servicoRepository = servicoRepository;
    }

    public Agendamento criar(AgendamentoRequest request) {
        Agendamento agendamento = new Agendamento();
        preencher(agendamento, request);
        agendamento.setStatus(StatusAgendamento.AGENDADO);
        return agendamentoRepository.save(agendamento);
    }

    @Transactional(readOnly = true)
    public List<Agendamento> listar(boolean incluirCancelados) {
        return incluirCancelados
                ? agendamentoRepository.findAll()
                : agendamentoRepository.findByStatusOrderByDataHoraInicioAsc(StatusAgendamento.AGENDADO);
    }

    @Transactional(readOnly = true)
    public Agendamento buscar(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<Agendamento> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null || fim.isBefore(inicio)) {
            throw new RegraNegocioException("Informe um período válido: a data final deve ser igual ou posterior à inicial");
        }
        return agendamentoRepository.findByDataHoraInicioBetweenOrderByDataHoraInicioAsc(inicio, fim);
    }

    @Transactional(readOnly = true)
    public List<Agendamento> listarPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new RecursoNaoEncontradoException("Cliente não encontrado: " + clienteId);
        }
        return agendamentoRepository.findByClienteIdOrderByDataHoraInicioDesc(clienteId);
    }

    public Agendamento atualizar(Long id, AgendamentoRequest request) {
        Agendamento agendamento = buscar(id);
        if (agendamento.getStatus() == StatusAgendamento.CANCELADO) {
            throw new RegraNegocioException("Não é possível atualizar um agendamento cancelado");
        }
        preencher(agendamento, request);
        return agendamentoRepository.save(agendamento);
    }

    public void cancelar(Long id) {
        Agendamento agendamento = buscar(id);
        if (agendamento.getStatus() == StatusAgendamento.CANCELADO) {
            throw new RegraNegocioException("Este agendamento já está cancelado");
        }
        agendamento.setStatus(StatusAgendamento.CANCELADO);
        agendamentoRepository.save(agendamento);
    }

    private void preencher(Agendamento agendamento, AgendamentoRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado: " + request.clienteId()));
        Servico servico = servicoRepository.findById(request.servicoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado: " + request.servicoId()));

        if (!cliente.isAtivo()) {
            throw new RegraNegocioException("Não é possível agendar para um cliente inativo");
        }
        if (!servico.isAtivo()) {
            throw new RegraNegocioException("Não é possível agendar um serviço inativo");
        }

        validarDisponibilidade(request.dataHoraInicio(), servico.getDuracaoMinutos(), agendamento.getId());
        agendamento.setCliente(cliente);
        agendamento.setServico(servico);
        agendamento.setDataHoraInicio(request.dataHoraInicio());
        agendamento.setObservacoes(request.observacoes());
    }

    private void validarDisponibilidade(LocalDateTime novoInicio, int novaDuracao, Long idIgnorado) {
        LocalDateTime novoFim = novoInicio.plusMinutes(novaDuracao);
        List<Agendamento> ativosAnteriores = agendamentoRepository
                .findByStatusAndDataHoraInicioLessThanAndIdNot(StatusAgendamento.AGENDADO, novoFim, idIgnorado == null ? -1L : idIgnorado);

        boolean conflita = ativosAnteriores.stream().anyMatch(existente ->
                existente.getDataHoraInicio().plusMinutes(existente.getServico().getDuracaoMinutos()).isAfter(novoInicio));
        if (conflita) {
            throw new RegraNegocioException("Este horário conflita com outro agendamento ativo. Escolha outro horário.");
        }
    }
}

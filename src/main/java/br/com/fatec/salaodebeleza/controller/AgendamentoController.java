package br.com.fatec.salaodebeleza.controller;

import br.com.fatec.salaodebeleza.dto.AgendamentoRequest;
import br.com.fatec.salaodebeleza.model.Agendamento;
import br.com.fatec.salaodebeleza.service.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/agendamentos")
@Tag(name = "Agendamentos", description = "Criação, consulta, atualização e cancelamento de horários")
public class AgendamentoController {
    private final AgendamentoService service;

    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Agendar horário", description = "Valida os vínculos, a data futura e os conflitos de horário.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Agendamento criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou data no passado"),
            @ApiResponse(responseCode = "404", description = "Cliente ou serviço inexistente"),
            @ApiResponse(responseCode = "409", description = "Horário em conflito ou cliente/serviço inativo")
    })
    public Agendamento criar(@Valid @RequestBody AgendamentoRequest request) {
        return service.criar(request);
    }

    @GetMapping
    @Operation(summary = "Listar agendamentos", description = "Por padrão, mostra apenas os ativos. Use incluirCancelados=true para ver todo o histórico.")
    public List<Agendamento> listar(@RequestParam(defaultValue = "false") boolean incluirCancelados) {
        return service.listar(incluirCancelados);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar agendamento por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Agendamento inexistente")
    })
    public Agendamento buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/periodo")
    @Operation(summary = "Consultar agendamentos por período", description = "Informe as datas no formato yyyy-MM-dd'T'HH:mm:ss.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamentos do período"),
            @ApiResponse(responseCode = "400", description = "Período inválido")
    })
    public List<Agendamento> listarPorPeriodo(
            @Parameter(example = "2026-11-25T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(example = "2026-11-25T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return service.listarPorPeriodo(inicio, fim);
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar histórico de agendamentos do cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Histórico do cliente"),
            @ApiResponse(responseCode = "404", description = "Cliente inexistente")
    })
    public List<Agendamento> listarPorCliente(@PathVariable Long clienteId) {
        return service.listarPorCliente(clienteId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Alterar agendamento", description = "Atualiza cliente, serviço, horário e observações se o novo horário estiver disponível.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou data no passado"),
            @ApiResponse(responseCode = "404", description = "Agendamento, cliente ou serviço inexistente"),
            @ApiResponse(responseCode = "409", description = "Horário em conflito, recurso inativo ou agendamento cancelado")
    })
    public Agendamento atualizar(@PathVariable Long id, @Valid @RequestBody AgendamentoRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cancelar agendamento", description = "Marca como CANCELADO e mantém o registro para consulta histórica.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Agendamento cancelado"),
            @ApiResponse(responseCode = "404", description = "Agendamento inexistente"),
            @ApiResponse(responseCode = "409", description = "Agendamento já cancelado")
    })
    public void cancelar(@PathVariable Long id) {
        service.cancelar(id);
    }
}

package br.com.fatec.salaodebeleza.controller;

import br.com.fatec.salaodebeleza.model.Servico;
import br.com.fatec.salaodebeleza.service.ServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicos")
@Tag(name = "Serviços", description = "Catálogo de serviços oferecidos pelo salão")
public class ServicoController {
    private final ServicoService service;

    public ServicoController(ServicoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar serviço", description = "Cadastra serviço, duração em minutos e preço.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Serviço cadastrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public Servico criar(@Valid @RequestBody Servico servico) {
        return service.criar(servico);
    }

    @GetMapping
    @Operation(summary = "Listar serviços")
    public List<Servico> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar serviço por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Serviço encontrado e ativo"),
            @ApiResponse(responseCode = "404", description = "Serviço inexistente ou inativo")
    })
    public Servico buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar serviço")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Serviço atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Serviço inexistente ou inativo")
    })
    public Servico atualizar(@PathVariable Long id, @Valid @RequestBody Servico servico) {
        return service.atualizar(id, servico);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Inativar serviço", description = "Retira da listagem e impede novos agendamentos, preservando o histórico relacionado.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Serviço inativado"),
            @ApiResponse(responseCode = "404", description = "Serviço inexistente ou já inativo")
    })
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }

    @PostMapping("/{id}/reativar")
    @Operation(summary = "Reativar serviço", description = "Permite que o serviço volte a aparecer nas listagens e receba agendamentos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Serviço reativado"),
            @ApiResponse(responseCode = "404", description = "Serviço inexistente"),
            @ApiResponse(responseCode = "409", description = "Serviço já está ativo")
    })
    public Servico reativar(@PathVariable Long id) {
        return service.reativar(id);
    }
}

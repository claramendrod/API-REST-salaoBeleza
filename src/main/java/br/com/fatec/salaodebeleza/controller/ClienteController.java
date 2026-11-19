package br.com.fatec.salaodebeleza.controller;

import br.com.fatec.salaodebeleza.model.Cliente;
import br.com.fatec.salaodebeleza.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@Tag(name = "Clientes", description = "Cadastro e consulta de clientes")
public class ClienteController {
    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar cliente", description = "Cadastra um cliente com nome e telefone obrigatórios.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente cadastrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public Cliente criar(@Valid @RequestBody Cliente cliente) {
        return service.criar(cliente);
    }

    @GetMapping
    @Operation(summary = "Listar clientes")
    public List<Cliente> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado e ativo"),
            @ApiResponse(responseCode = "404", description = "Cliente inexistente ou inativo")
    })
    public Cliente buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente inexistente ou inativo")
    })
    public Cliente atualizar(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        return service.atualizar(id, cliente);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Inativar cliente", description = "Retira da listagem e impede novos agendamentos, preservando o histórico relacionado.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente inativado"),
            @ApiResponse(responseCode = "404", description = "Cliente inexistente ou já inativo")
    })
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }

    @PostMapping("/{id}/reativar")
    @Operation(summary = "Reativar cliente", description = "Permite que o cliente volte a aparecer nas listagens e receba agendamentos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente reativado"),
            @ApiResponse(responseCode = "404", description = "Cliente inexistente"),
            @ApiResponse(responseCode = "409", description = "Cliente já está ativo")
    })
    public Cliente reativar(@PathVariable Long id) {
        return service.reativar(id);
    }
}

package br.com.fatec.salaodebeleza.service;

import br.com.fatec.salaodebeleza.model.Cliente;
import br.com.fatec.salaodebeleza.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClienteService {
    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public Cliente criar(Cliente cliente) {
        cliente.setId(null);
        cliente.setAtivo(true);
        return repository.save(cliente);
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return repository.findAllByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public Cliente buscar(Long id) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado: " + id));
        if (!cliente.isAtivo()) {
            throw new RecursoNaoEncontradoException("Cliente não encontrado: " + id);
        }
        return cliente;
    }

    public Cliente atualizar(Long id, Cliente dados) {
        Cliente cliente = buscar(id);
        if (!cliente.isAtivo()) {
            throw new RegraNegocioException("Não é possível atualizar um cliente inativo");
        }
        cliente.setNome(dados.getNome());
        cliente.setTelefone(dados.getTelefone());
        cliente.setEmail(dados.getEmail());
        return repository.save(cliente);
    }

    public void excluir(Long id) {
        Cliente cliente = buscar(id);
        if (!cliente.isAtivo()) {
            throw new RegraNegocioException("Este cliente já está inativo");
        }
        cliente.setAtivo(false);
        repository.save(cliente);
    }

    public Cliente reativar(Long id) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado: " + id));
        if (cliente.isAtivo()) {
            throw new RegraNegocioException("Este cliente já está ativo");
        }
        cliente.setAtivo(true);
        return repository.save(cliente);
    }
}

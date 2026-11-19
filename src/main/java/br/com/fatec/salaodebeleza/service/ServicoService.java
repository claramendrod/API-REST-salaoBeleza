package br.com.fatec.salaodebeleza.service;

import br.com.fatec.salaodebeleza.model.Servico;
import br.com.fatec.salaodebeleza.repository.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ServicoService {
    private final ServicoRepository repository;

    public ServicoService(ServicoRepository repository) {
        this.repository = repository;
    }

    public Servico criar(Servico servico) {
        servico.setId(null);
        servico.setAtivo(true);
        return repository.save(servico);
    }

    @Transactional(readOnly = true)
    public List<Servico> listar() {
        return repository.findAllByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public Servico buscar(Long id) {
        Servico servico = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado: " + id));
        if (!servico.isAtivo()) {
            throw new RecursoNaoEncontradoException("Serviço não encontrado: " + id);
        }
        return servico;
    }

    public Servico atualizar(Long id, Servico dados) {
        Servico servico = buscar(id);
        if (!servico.isAtivo()) {
            throw new RegraNegocioException("Não é possível atualizar um serviço inativo");
        }
        servico.setNome(dados.getNome());
        servico.setDescricao(dados.getDescricao());
        servico.setDuracaoMinutos(dados.getDuracaoMinutos());
        servico.setPreco(dados.getPreco());
        return repository.save(servico);
    }

    public void excluir(Long id) {
        Servico servico = buscar(id);
        if (!servico.isAtivo()) {
            throw new RegraNegocioException("Este serviço já está inativo");
        }
        servico.setAtivo(false);
        repository.save(servico);
    }

    public Servico reativar(Long id) {
        Servico servico = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado: " + id));
        if (servico.isAtivo()) {
            throw new RegraNegocioException("Este serviço já está ativo");
        }
        servico.setAtivo(true);
        return repository.save(servico);
    }
}

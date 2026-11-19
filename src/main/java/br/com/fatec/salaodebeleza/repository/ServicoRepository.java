package br.com.fatec.salaodebeleza.repository;

import br.com.fatec.salaodebeleza.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Long> {
    List<Servico> findAllByAtivoTrue();
}

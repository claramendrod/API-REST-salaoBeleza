package br.com.fatec.salaodebeleza.repository;

import br.com.fatec.salaodebeleza.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findAllByAtivoTrue();
}

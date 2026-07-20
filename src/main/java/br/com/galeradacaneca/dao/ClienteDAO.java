package br.com.galeradacaneca.dao;

import br.com.galeradacaneca.model.Cliente;
import java.util.List;
import java.util.Optional;

/**
 * Contrato para acesso a dados de Cliente.
 *
 * SOLID — ISP: segregado em interface própria, contendo apenas métodos
 * relevantes para o domínio de clientes.
 *
 * SOLID — DIP: ClienteService depende desta abstração.
 */
public interface ClienteDAO extends GenericDAO<Cliente, Integer> {

    List<Cliente> pesquisarPorNome(String nome);

    Optional<Cliente> buscarPorCpf(String cpf);
}

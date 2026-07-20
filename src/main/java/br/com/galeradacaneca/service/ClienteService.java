package br.com.galeradacaneca.service;

import br.com.galeradacaneca.model.Cliente;
import java.util.List;
import java.util.Optional;

/**
 * Contrato da camada de serviço para Cliente.
 *
 * SOLID — ISP: interface enxuta, apenas operações de negócio de clientes.
 * SOLID — DIP: controllers/views dependem desta abstração.
 */
public interface ClienteService {

    void cadastrar(Cliente cliente);

    void atualizar(Cliente cliente);

    void excluir(int id);

    Optional<Cliente> buscarPorId(int id);

    List<Cliente> listarTodos();

    List<Cliente> pesquisarPorNome(String nome);

    Optional<Cliente> buscarPorCpf(String cpf);
}

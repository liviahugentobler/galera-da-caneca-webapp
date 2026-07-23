package br.com.galeradacaneca.service.impl;

import br.com.galeradacaneca.dao.ClienteDAO;
import br.com.galeradacaneca.model.Cliente;
import br.com.galeradacaneca.service.ClienteService;

import java.util.List;
import java.util.Optional;

/**
 * Implementação da camada de serviço para Cliente.
 *
 * SOLID — SRP: contém apenas regras de negócio relacionadas a clientes.
 *   Persistência delegada ao ClienteDAO.
 *
 * SOLID — DIP: depende da abstração ClienteDAO, não de uma implementação concreta.
 *   A implementação concreta é injetada pelo construtor (Injeção de Dependência).
 *
 * Padrão de Projeto — Facade:
 *   Esta classe atua como fachada entre a camada de apresentação (futura
 *   interface web) e a camada de acesso a dados, simplificando o uso e
 *   centralizando as validações de negócio.
 *
 * Refatorações aplicadas:
 *   - Validações extraídas das views Swing (code smell "God Class" nas views).
 *   - Lançamento de IllegalArgumentException para erros de negócio, em vez
 *     de manipular JOptionPane diretamente no DAO.
 */
public class ClienteServiceImpl implements ClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteServiceImpl(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    @Override
    public void cadastrar(Cliente cliente) {
        validar(cliente);
        clienteDAO.salvar(cliente);
    }

    @Override
    public void atualizar(Cliente cliente) {
        if (cliente.getId() == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo para atualização.");
        }
        validar(cliente);
        clienteDAO.atualizar(cliente);
    }

    @Override
    public void excluir(int id) {
        clienteDAO.excluir(id);
    }

    @Override
    public Optional<Cliente> buscarPorId(int id) {
        return clienteDAO.buscarPorId(id);
    }

    @Override
    public List<Cliente> listarTodos() {
        return clienteDAO.listarTodos();
    }

    @Override
    public List<Cliente> pesquisarPorNome(String nome) {
        if (nome == null || nome.isBlank()) return listarTodos();
        return clienteDAO.pesquisarPorNome(nome);
    }

    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        return clienteDAO.buscarPorCpf(cpf);
    }

    private String limparCpf(String cpf) {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }

    private void validar(Cliente c) {
        if (c.getNomeCompleto() == null || c.getNomeCompleto().isBlank())
            throw new IllegalArgumentException("Nome completo é obrigatório.");
        if (c.getCpf() == null || c.getCpf().isBlank())
            throw new IllegalArgumentException("CPF é obrigatório.");
        String cpf = limparCpf(c.getCpf());

        if (!cpf.matches("\\d{11}"))
            throw new IllegalArgumentException("CPF deve conter 11 dígitos numéricos.");
        c.setCpf(cpf);

        if (c.getEmail() == null || c.getEmail().isBlank())
            throw new IllegalArgumentException("E-mail é obrigatório.");
        if (!c.getEmail().contains("@"))
            throw new IllegalArgumentException("E-mail inválido.");
        if (c.getNascimento() == null)
            throw new IllegalArgumentException("Data de nascimento é obrigatória.");
        if (c.getTelefone() == null || c.getTelefone().isBlank())
            throw new IllegalArgumentException("Telefone é obrigatório.");
        if (c.getEndereco() == null || c.getEndereco().isBlank())
            throw new IllegalArgumentException("Endereço é obrigatório.");
        if (c.getSenha() == null || c.getSenha().isBlank())
            throw new IllegalArgumentException("Senha é obrigatória.");
    }
}

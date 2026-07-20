package br.com.galeradacaneca.service.impl;

import br.com.galeradacaneca.dao.VendedorDAO;
import br.com.galeradacaneca.model.Vendedor;
import br.com.galeradacaneca.service.VendedorService;

import java.util.List;
import java.util.Optional;

/**
 * Implementação da camada de serviço para Vendedor.
 *
 * SOLID — SRP: regras de negócio de funcionários/vendedores centralizadas aqui.
 * SOLID — DIP: depende da abstração VendedorDAO.
 *
 * Padrão de Projeto — Facade.
 *
 * Refatorações aplicadas:
 *   - Validações extraídas das views Swing.
 *   - autenticar() retorna Optional (elimina null return — code smell).
 */
public class VendedorServiceImpl implements VendedorService {

    private final VendedorDAO vendedorDAO;

    public VendedorServiceImpl(VendedorDAO vendedorDAO) {
        this.vendedorDAO = vendedorDAO;
    }

    @Override
    public void cadastrar(Vendedor vendedor) {
        validar(vendedor);
        vendedorDAO.salvar(vendedor);
    }

    @Override
    public void atualizar(Vendedor vendedor) {
        if (vendedor.getId() == null)
            throw new IllegalArgumentException("ID do vendedor não pode ser nulo para atualização.");
        validar(vendedor);
        vendedorDAO.atualizar(vendedor);
    }

    @Override
    public void excluir(int id) {
        vendedorDAO.excluir(id);
    }

    @Override
    public Optional<Vendedor> buscarPorId(int id) {
        return vendedorDAO.buscarPorId(id);
    }

    @Override
    public List<Vendedor> listarTodos() {
        return vendedorDAO.listarTodos();
    }

    @Override
    public List<Vendedor> pesquisarPorNome(String nome) {
        if (nome == null || nome.isBlank()) return listarTodos();
        return vendedorDAO.pesquisarPorNome(nome);
    }

    @Override
    public Optional<Vendedor> autenticar(String email, String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isBlank())
            return Optional.empty();
        return vendedorDAO.autenticar(email, senha);
    }

    @Override
    public void alterarSenha(int idVendedor, String novaSenha) {
        if (novaSenha == null || novaSenha.length() < 4)
            throw new IllegalArgumentException("A nova senha deve ter pelo menos 4 caracteres.");
        vendedorDAO.alterarSenha(idVendedor, novaSenha);
    }

    @Override
    public double totalVendasPorVendedor(int idVendedor) {
        return vendedorDAO.totalVendasPorVendedor(idVendedor);
    }

    private void validar(Vendedor v) {
        if (v.getNomeCompleto() == null || v.getNomeCompleto().isBlank())
            throw new IllegalArgumentException("Nome completo é obrigatório.");
        if (v.getCpf() == null || !v.getCpf().matches("\\d{11}"))
            throw new IllegalArgumentException("CPF deve conter 11 dígitos numéricos.");
        if (v.getEmail() == null || !v.getEmail().contains("@"))
            throw new IllegalArgumentException("E-mail inválido.");
        if (v.getNascimento() == null)
            throw new IllegalArgumentException("Data de nascimento é obrigatória.");
        if (v.getSenha() == null || v.getSenha().isBlank())
            throw new IllegalArgumentException("Senha é obrigatória.");
        if (v.getCargo() == null)
            throw new IllegalArgumentException("Cargo é obrigatório.");
    }
}

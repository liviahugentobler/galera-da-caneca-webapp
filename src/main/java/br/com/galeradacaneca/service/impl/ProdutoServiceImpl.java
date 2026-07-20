package br.com.galeradacaneca.service.impl;

import br.com.galeradacaneca.dao.ProdutoDAO;
import br.com.galeradacaneca.model.Produto;
import br.com.galeradacaneca.service.ProdutoService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Implementação da camada de serviço para Produto.
 *
 * SOLID — SRP: regras de negócio de produtos centralizadas aqui.
 * SOLID — DIP: depende da abstração ProdutoDAO.
 *
 * Padrão de Projeto — Facade.
 *
 * Refatorações aplicadas:
 *   - Validações extraídas das views Swing.
 *   - Verificação de estoque negativo centralizada no serviço.
 */
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoDAO produtoDAO;

    public ProdutoServiceImpl(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    @Override
    public void cadastrar(Produto produto) {
        validar(produto);
        if (produto.getQuantidade() == null) produto.setQuantidade(0);
        if (produto.getSaidas() == null)     produto.setSaidas(0);
        if (produto.getEntradas() == null)   produto.setEntradas(0);
        produtoDAO.salvar(produto);
    }

    @Override
    public void atualizar(Produto produto) {
        if (produto.getId() == null)
            throw new IllegalArgumentException("ID do produto não pode ser nulo para atualização.");
        validar(produto);
        produtoDAO.atualizar(produto);
    }

    @Override
    public void excluir(int id) {
        produtoDAO.excluir(id);
    }

    @Override
    public Optional<Produto> buscarPorId(int id) {
        return produtoDAO.buscarPorId(id);
    }

    @Override
    public List<Produto> listarTodos() {
        return produtoDAO.listarTodos();
    }

    @Override
    public List<Produto> pesquisarPorNome(String nome) {
        if (nome == null || nome.isBlank()) return listarTodos();
        return produtoDAO.pesquisarPorNome(nome);
    }

    @Override
    public void darBaixaEstoque(int idProduto, int quantidade) {
        if (quantidade <= 0)
            throw new IllegalArgumentException("Quantidade de saída deve ser maior que zero.");
        produtoDAO.darBaixaEstoque(idProduto, quantidade);
    }

    @Override
    public void registrarEntrada(int idProduto, int quantidade) {
        if (quantidade <= 0)
            throw new IllegalArgumentException("Quantidade de entrada deve ser maior que zero.");
        produtoDAO.registrarEntrada(idProduto, quantidade);
    }

    @Override
    public void atualizarPreco(int idProduto, BigDecimal novoPreco) {
        if (novoPreco == null || novoPreco.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Preço não pode ser nulo ou negativo.");
        produtoDAO.atualizarPreco(idProduto, novoPreco);
    }

    // ── Validações ────────────────────────────────────────────────────────────

    private void validar(Produto p) {
        if (p.getNomeProd() == null || p.getNomeProd().isBlank())
            throw new IllegalArgumentException("Nome do produto é obrigatório.");
        if (p.getPreco() == null || p.getPreco().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Preço inválido.");
    }
}

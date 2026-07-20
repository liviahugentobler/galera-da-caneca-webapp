package br.com.galeradacaneca.service;

import br.com.galeradacaneca.model.Produto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Contrato da camada de serviço para Produto.
 *
 * SOLID — ISP / DIP.
 */
public interface ProdutoService {

    void cadastrar(Produto produto);

    void atualizar(Produto produto);

    void excluir(int id);

    Optional<Produto> buscarPorId(int id);

    List<Produto> listarTodos();

    List<Produto> pesquisarPorNome(String nome);

    void darBaixaEstoque(int idProduto, int quantidade);

    void registrarEntrada(int idProduto, int quantidade);

    void atualizarPreco(int idProduto, BigDecimal novoPreco);
}

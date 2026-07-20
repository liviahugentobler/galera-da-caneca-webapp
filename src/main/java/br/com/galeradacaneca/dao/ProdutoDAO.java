package br.com.galeradacaneca.dao;

import br.com.galeradacaneca.model.Produto;
import java.math.BigDecimal;
import java.util.List;

/**
 * Contrato para acesso a dados de Produto.
 *
 * SOLID — ISP / DIP.
 */
public interface ProdutoDAO extends GenericDAO<Produto, Integer> {

    List<Produto> pesquisarPorNome(String nome);

    void darBaixaEstoque(int idProduto, int qtdSaida);

    void registrarEntrada(int idProduto, int qtdEntrada);

    void atualizarPreco(int idProduto, BigDecimal novoPreco);
}

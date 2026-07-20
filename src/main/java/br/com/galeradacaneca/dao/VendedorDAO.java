package br.com.galeradacaneca.dao;

import br.com.galeradacaneca.model.Vendedor;
import java.util.List;
import java.util.Optional;

/**
 * Contrato para acesso a dados de Vendedor.
 *
 * SOLID — ISP / DIP.
 */
public interface VendedorDAO extends GenericDAO<Vendedor, Integer> {

    List<Vendedor> pesquisarPorNome(String nome);

    Optional<Vendedor> autenticar(String email, String senha);

    void alterarSenha(int idVendedor, String novaSenha);

    double totalVendasPorVendedor(int idVendedor);
}

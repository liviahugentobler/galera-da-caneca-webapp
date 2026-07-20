package br.com.galeradacaneca.service;

import br.com.galeradacaneca.model.Vendedor;
import java.util.List;
import java.util.Optional;

/**
 * Contrato da camada de serviço para Vendedor.
 *
 * SOLID — ISP / DIP.
 */
public interface VendedorService {

    void cadastrar(Vendedor vendedor);

    void atualizar(Vendedor vendedor);

    void excluir(int id);

    Optional<Vendedor> buscarPorId(int id);

    List<Vendedor> listarTodos();

    List<Vendedor> pesquisarPorNome(String nome);

    Optional<Vendedor> autenticar(String email, String senha);

    void alterarSenha(int idVendedor, String novaSenha);

    double totalVendasPorVendedor(int idVendedor);
}

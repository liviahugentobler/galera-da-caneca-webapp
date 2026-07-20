package br.com.galeradacaneca.dao;

import br.com.galeradacaneca.model.Pagamento;
import java.util.Optional;

/**
 * Contrato para acesso a dados de Pagamento.
 *
 * SOLID — ISP / DIP.
 */
public interface PagamentoDAO extends GenericDAO<Pagamento, Integer> {

    Optional<Pagamento> buscarPorVenda(int idVenda);
}

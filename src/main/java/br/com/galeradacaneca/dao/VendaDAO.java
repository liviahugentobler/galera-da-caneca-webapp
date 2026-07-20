package br.com.galeradacaneca.dao;

import br.com.galeradacaneca.model.Venda;
import java.math.BigDecimal;
import java.util.List;

/**
 * Contrato para acesso a dados de Venda.
 *
 * SOLID — ISP / DIP.
 */
public interface VendaDAO extends GenericDAO<Venda, Integer> {

    List<Venda> listarPorVendedor(int idVendedor);

    BigDecimal totalGeral();

    BigDecimal totalPorVendedor(int idVendedor);

    long contarTodas();

    long contarPorVendedor(int idVendedor);
}

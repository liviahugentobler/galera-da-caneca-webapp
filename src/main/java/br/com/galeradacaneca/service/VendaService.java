package br.com.galeradacaneca.service;

import br.com.galeradacaneca.model.Venda;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Contrato da camada de serviço para Venda.
 *
 * SOLID — ISP / DIP.
 */
public interface VendaService {

    void registrar(Venda venda);

    void atualizar(Venda venda);

    void excluir(int id);

    Optional<Venda> buscarPorId(int id);

    List<Venda> listarTodas();

    List<Venda> listarPorVendedor(int idVendedor);

    BigDecimal totalGeral();

    BigDecimal totalPorVendedor(int idVendedor);

    long contarTodas();

    long contarPorVendedor(int idVendedor);
}

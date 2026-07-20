package br.com.galeradacaneca.service;

import br.com.galeradacaneca.model.Pagamento;
import java.util.Optional;

/**
 * Contrato da camada de serviço para Pagamento.
 *
 * SOLID — ISP / DIP.
 */
public interface PagamentoService {

    void registrar(Pagamento pagamento);

    void excluir(int id);

    Optional<Pagamento> buscarPorVenda(int idVenda);
}

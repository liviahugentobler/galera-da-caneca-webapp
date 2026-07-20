package br.com.galeradacaneca.service.impl;

import br.com.galeradacaneca.dao.PagamentoDAO;
import br.com.galeradacaneca.model.Pagamento;
import br.com.galeradacaneca.service.PagamentoService;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Implementação da camada de serviço para Pagamento.
 *
 * SOLID — SRP / DIP.
 * Padrão de Projeto — Facade.
 */
public class PagamentoServiceImpl implements PagamentoService {

    private static final java.util.Set<String> FORMAS_VALIDAS =
        java.util.Set.of("Dinheiro", "Cartão", "Pix", "Boleto");

    private final PagamentoDAO pagamentoDAO;

    public PagamentoServiceImpl(PagamentoDAO pagamentoDAO) {
        this.pagamentoDAO = pagamentoDAO;
    }

    @Override
    public void registrar(Pagamento pagamento) {
        validar(pagamento);
        pagamentoDAO.salvar(pagamento);
    }

    @Override
    public void excluir(int id) {
        pagamentoDAO.excluir(id);
    }

    @Override
    public Optional<Pagamento> buscarPorVenda(int idVenda) {
        return pagamentoDAO.buscarPorVenda(idVenda);
    }

    private void validar(Pagamento p) {
        if (p.getVenda() == null)
            throw new IllegalArgumentException("O pagamento deve estar associado a uma venda.");
        if (p.getValorTotal() == null || p.getValorTotal().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Valor do pagamento deve ser maior que zero.");
        if (p.getFormaPagamento() == null || !FORMAS_VALIDAS.contains(p.getFormaPagamento()))
            throw new IllegalArgumentException("Forma de pagamento inválida. Use: " + FORMAS_VALIDAS);
    }
}

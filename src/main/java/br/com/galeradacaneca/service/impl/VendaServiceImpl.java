package br.com.galeradacaneca.service.impl;

import br.com.galeradacaneca.dao.VendaDAO;
import br.com.galeradacaneca.model.Venda;
import br.com.galeradacaneca.service.VendaService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Implementação da camada de serviço para Venda.
 *
 * SOLID — SRP / DIP.
 * Padrão de Projeto — Facade.
 */
public class VendaServiceImpl implements VendaService {

    private final VendaDAO vendaDAO;

    public VendaServiceImpl(VendaDAO vendaDAO) {
        this.vendaDAO = vendaDAO;
    }

    @Override
    public void registrar(Venda venda) {
        validar(venda);
        vendaDAO.salvar(venda);
    }

    @Override
    public void atualizar(Venda venda) {
        if (venda.getId() == null)
            throw new IllegalArgumentException("ID da venda não pode ser nulo para atualização.");
        validar(venda);
        vendaDAO.atualizar(venda);
    }

    @Override
    public void excluir(int id) {
        vendaDAO.excluir(id);
    }

    @Override
    public Optional<Venda> buscarPorId(int id) {
        return vendaDAO.buscarPorId(id);
    }

    @Override
    public List<Venda> listarTodas() {
        return vendaDAO.listarTodos();
    }

    @Override
    public List<Venda> listarPorVendedor(int idVendedor) {
        return vendaDAO.listarPorVendedor(idVendedor);
    }

    @Override
    public BigDecimal totalGeral() {
        return vendaDAO.totalGeral();
    }

    @Override
    public BigDecimal totalPorVendedor(int idVendedor) {
        return vendaDAO.totalPorVendedor(idVendedor);
    }

    @Override
    public long contarTodas() {
        return vendaDAO.contarTodas();
    }

    @Override
    public long contarPorVendedor(int idVendedor) {
        return vendaDAO.contarPorVendedor(idVendedor);
    }

    private void validar(Venda v) {
        if (v.getCliente() == null)
            throw new IllegalArgumentException("A venda deve ter um cliente associado.");
        if (v.getValorTotal() == null || v.getValorTotal().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Valor total da venda deve ser maior que zero.");
    }
}

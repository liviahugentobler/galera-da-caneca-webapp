package br.com.galeradacaneca.web.servlet;

import br.com.galeradacaneca.model.*;
import br.com.galeradacaneca.service.CalculadoraDeVenda;
import br.com.galeradacaneca.web.AppContext;
import br.com.galeradacaneca.web.util.JsonUtil;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Endpoints REST para Venda:
 *   GET    /api/vendas?vendedorId=&cliente=   -> lista (com filtros opcionais)
 *   GET    /api/vendas/{id}                   -> uma venda
 *   POST   /api/vendas                        -> registra venda + pagamento + baixa de estoque
 *   DELETE /api/vendas/{id}                   -> exclui venda (devolve estoque)
 *
 * Concentra, na camada web, a orquestração entre VendaService, ProdutoService
 * e PagamentoService — mantendo cada Service focado em sua própria entidade
 * (SRP), enquanto o servlet cumpre o papel de "Controller" da arquitetura MVC.
 */
@WebServlet(urlPatterns = {"/api/vendas/*"})
public class VendaServlet extends HttpServlet {

    private final CalculadoraDeVenda calculadora = new CalculadoraDeVenda();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = idDoCaminho(req.getPathInfo());
        try {
            if (id == null) {
                String vendedorIdParam = req.getParameter("vendedorId");
                String clienteFiltro = req.getParameter("cliente");

                List<Venda> lista = (vendedorIdParam == null || vendedorIdParam.isBlank())
                        ? AppContext.VENDA_SERVICE.listarTodas()
                        : AppContext.VENDA_SERVICE.listarPorVendedor(Integer.parseInt(vendedorIdParam));

                if (clienteFiltro != null && !clienteFiltro.isBlank()) {
                    String termo = clienteFiltro.toLowerCase();
                    lista = lista.stream()
                            .filter(v -> v.getCliente() != null && v.getCliente().getNomeCompleto() != null
                                    && v.getCliente().getNomeCompleto().toLowerCase().contains(termo))
                            .collect(Collectors.toList());
                }

                JsonUtil.escrever(resp, 200, lista.stream().map(this::paraJson).collect(Collectors.toList()));
            } else {
                Optional<Venda> v = AppContext.VENDA_SERVICE.buscarPorId(id);
                if (v.isEmpty()) { JsonUtil.erro(resp, 404, "Venda não encontrada."); return; }
                JsonUtil.escrever(resp, 200, paraJson(v.get()));
            }
        } catch (Exception e) {
            JsonUtil.erro(resp, 500, "Erro ao consultar vendas: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            JsonObject corpo = JsonUtil.lerCorpo(req);
            Integer clienteId  = JsonUtil.getInt(corpo, "clienteId");
            Integer produtoId  = JsonUtil.getInt(corpo, "produtoId");
            Integer vendedorId = JsonUtil.getInt(corpo, "vendedorId");
            Integer quantidade = JsonUtil.getInt(corpo, "quantidade");
            String formaPagamento = JsonUtil.getString(corpo, "formaPagamento");
            if (formaPagamento == null || formaPagamento.isBlank()) formaPagamento = "Dinheiro";

            if (clienteId == null || produtoId == null || quantidade == null || quantidade <= 0) {
                JsonUtil.erro(resp, 400, "Informe cliente, produto e uma quantidade válida.");
                return;
            }

            Optional<Cliente> clienteOpt = AppContext.CLIENTE_SERVICE.buscarPorId(clienteId);
            if (clienteOpt.isEmpty()) { JsonUtil.erro(resp, 400, "Cliente informado não existe."); return; }

            Optional<Produto> produtoOpt = AppContext.PRODUTO_SERVICE.buscarPorId(produtoId);
            if (produtoOpt.isEmpty()) { JsonUtil.erro(resp, 400, "Produto informado não existe."); return; }
            Produto produto = produtoOpt.get();

            if (produto.getQuantidade() == null || produto.getQuantidade() < quantidade) {
                JsonUtil.erro(resp, 400, "Estoque insuficiente. Disponível: "
                        + (produto.getQuantidade() == null ? 0 : produto.getQuantidade()) + " unidade(s).");
                return;
            }

            Vendedor vendedor = null;
            if (vendedorId != null) {
                vendedor = AppContext.VENDEDOR_SERVICE.buscarPorId(vendedorId).orElse(null);
            }

            BigDecimal total = calculadora.calcularValorTotalComDesconto(produto.getPreco(), quantidade);

            Venda venda = new Venda();
            venda.setCliente(clienteOpt.get());
            venda.setProduto(produto);
            venda.setVendedor(vendedor);
            venda.setQuantidade(quantidade);
            venda.setValorTotal(total);

            AppContext.VENDA_SERVICE.registrar(venda);
            AppContext.PRODUTO_SERVICE.darBaixaEstoque(produtoId, quantidade);

            Pagamento pagamento = new Pagamento();
            pagamento.setVenda(venda);
            pagamento.setValorTotal(total);
            pagamento.setFormaPagamento(formaPagamento);
            AppContext.PAGAMENTO_SERVICE.registrar(pagamento);

            JsonUtil.escrever(resp, 201, paraJson(venda));
        } catch (IllegalArgumentException e) {
            JsonUtil.erro(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.erro(resp, 500, "Erro ao registrar venda: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = idDoCaminho(req.getPathInfo());
        if (id == null) { JsonUtil.erro(resp, 400, "Informe o ID da venda na URL."); return; }
        try {
            Optional<Venda> vendaOpt = AppContext.VENDA_SERVICE.buscarPorId(id);
            if (vendaOpt.isEmpty()) { JsonUtil.erro(resp, 404, "Venda não encontrada."); return; }
            Venda venda = vendaOpt.get();

            AppContext.PAGAMENTO_SERVICE.buscarPorVenda(id)
                    .ifPresent(pg -> AppContext.PAGAMENTO_SERVICE.excluir(pg.getId()));

            if (venda.getProduto() != null && venda.getQuantidade() != null) {
                AppContext.PRODUTO_SERVICE.registrarEntrada(venda.getProduto().getId(), venda.getQuantidade());
            }

            AppContext.VENDA_SERVICE.excluir(id);
            JsonUtil.escrever(resp, 200, Map.of("mensagem", "Venda excluída com sucesso."));
        } catch (Exception e) {
            JsonUtil.erro(resp, 500, "Erro ao excluir venda: " + e.getMessage());
        }
    }

    private Integer idDoCaminho(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/")) return null;
        try { return Integer.valueOf(pathInfo.substring(1)); }
        catch (NumberFormatException e) { return null; }
    }

    private Map<String, Object> paraJson(Venda v) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", v.getId());
        m.put("clienteId", v.getCliente() == null ? null : v.getCliente().getId());
        m.put("clienteNome", v.getCliente() == null ? null : v.getCliente().getNomeCompleto());
        m.put("produtoId", v.getProduto() == null ? null : v.getProduto().getId());
        m.put("produtoNome", v.getProduto() == null ? null : v.getProduto().getNomeProd());
        m.put("vendedorId", v.getVendedor() == null ? null : v.getVendedor().getId());
        m.put("vendedorNome", v.getVendedor() == null ? null : v.getVendedor().getNomeCompleto());
        m.put("quantidade", v.getQuantidade());
        m.put("valorTotal", v.getValorTotal());
        m.put("dataVenda", v.getDataVenda());

        if (v.getProduto() != null && v.getProduto().getPreco() != null && v.getQuantidade() != null) {
            BigDecimal subtotal = calculadora.calcularSubtotal(v.getProduto().getPreco(), v.getQuantidade());
            BigDecimal percentual = calculadora.calcularPercentualDesconto(v.getQuantidade());
            m.put("precoUnitario", v.getProduto().getPreco());
            m.put("subtotal", subtotal);
            m.put("percentualDesconto", percentual.multiply(BigDecimal.valueOf(100)).intValue());
            m.put("valorDesconto", subtotal.subtract(v.getValorTotal() == null ? subtotal : v.getValorTotal()));
        }
        return m;
    }
}

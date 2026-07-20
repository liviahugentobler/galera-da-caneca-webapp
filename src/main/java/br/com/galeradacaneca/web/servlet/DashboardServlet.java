package br.com.galeradacaneca.web.servlet;

import br.com.galeradacaneca.model.Produto;
import br.com.galeradacaneca.model.Venda;
import br.com.galeradacaneca.web.AppContext;
import br.com.galeradacaneca.web.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * GET /api/dashboard?vendedorId=&isGerente=true|false
 * Retorna métricas agregadas para o painel principal.
 */
@WebServlet(urlPatterns = {"/api/dashboard"})
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            boolean isGerente = Boolean.parseBoolean(req.getParameter("isGerente"));
            String vendedorIdParam = req.getParameter("vendedorId");

            List<Venda> vendas;
            BigDecimal totalVendido;
            long qtdVendas;

            if (isGerente || vendedorIdParam == null || vendedorIdParam.isBlank()) {
                vendas = AppContext.VENDA_SERVICE.listarTodas();
                totalVendido = AppContext.VENDA_SERVICE.totalGeral();
                qtdVendas = AppContext.VENDA_SERVICE.contarTodas();
            } else {
                int vendedorId = Integer.parseInt(vendedorIdParam);
                vendas = AppContext.VENDA_SERVICE.listarPorVendedor(vendedorId);
                totalVendido = AppContext.VENDA_SERVICE.totalPorVendedor(vendedorId);
                qtdVendas = AppContext.VENDA_SERVICE.contarPorVendedor(vendedorId);
            }

            int unidades = vendas.stream().mapToInt(v -> v.getQuantidade() == null ? 0 : v.getQuantidade()).sum();

            List<Produto> produtos = AppContext.PRODUTO_SERVICE.listarTodos();
            long produtosBaixoEstoque = produtos.stream()
                    .filter(p -> p.getQuantidade() != null && p.getQuantidade() <= 10)
                    .count();

            Map<String, Object> corpo = new LinkedHashMap<>();
            corpo.put("totalVendido", totalVendido == null ? BigDecimal.ZERO : totalVendido);
            corpo.put("qtdVendas", qtdVendas);
            corpo.put("unidades", unidades);
            corpo.put("produtosBaixoEstoque", produtosBaixoEstoque);
            corpo.put("qtdVendedores", AppContext.VENDEDOR_SERVICE.listarTodos().size());

            List<Map<String, Object>> ultimas = vendas.stream()
                    .sorted((a, b) -> {
                        Integer ia = a.getId() == null ? 0 : a.getId();
                        Integer ib = b.getId() == null ? 0 : b.getId();
                        return ib.compareTo(ia);
                    })
                    .limit(5)
                    .map(v -> {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("id", v.getId());
                        m.put("dataVenda", v.getDataVenda());
                        m.put("clienteNome", v.getCliente() == null ? null : v.getCliente().getNomeCompleto());
                        m.put("produtoNome", v.getProduto() == null ? null : v.getProduto().getNomeProd());
                        m.put("quantidade", v.getQuantidade());
                        m.put("valorTotal", v.getValorTotal());
                        return m;
                    })
                    .collect(java.util.stream.Collectors.toList());
            corpo.put("ultimasVendas", ultimas);

            JsonUtil.escrever(resp, 200, corpo);
        } catch (Exception e) {
            JsonUtil.erro(resp, 500, "Erro ao carregar painel: " + e.getMessage());
        }
    }
}

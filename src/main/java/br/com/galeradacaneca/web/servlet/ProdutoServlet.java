package br.com.galeradacaneca.web.servlet;

import br.com.galeradacaneca.model.Produto;
import br.com.galeradacaneca.web.AppContext;
import br.com.galeradacaneca.web.util.JsonUtil;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Endpoints REST para Produto:
 *   GET    /api/produtos?nome=   -> lista (com filtro opcional)
 *   GET    /api/produtos/{id}    -> um produto
 *   POST   /api/produtos         -> cadastra
 *   PUT    /api/produtos/{id}    -> atualiza
 *   DELETE /api/produtos/{id}    -> exclui
 */
@WebServlet(urlPatterns = {"/api/produtos/*"})
public class ProdutoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = idDoCaminho(req.getPathInfo());
        try {
            if (id == null) {
                String nome = req.getParameter("nome");
                List<Produto> lista = (nome == null || nome.isBlank())
                        ? AppContext.PRODUTO_SERVICE.listarTodos()
                        : AppContext.PRODUTO_SERVICE.pesquisarPorNome(nome);
                JsonUtil.escrever(resp, 200, lista.stream().map(this::paraJson).collect(Collectors.toList()));
            } else {
                Optional<Produto> p = AppContext.PRODUTO_SERVICE.buscarPorId(id);
                if (p.isEmpty()) { JsonUtil.erro(resp, 404, "Produto não encontrado."); return; }
                JsonUtil.escrever(resp, 200, paraJson(p.get()));
            }
        } catch (Exception e) {
            JsonUtil.erro(resp, 500, "Erro ao consultar produtos: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            JsonObject corpo = JsonUtil.lerCorpo(req);
            Produto p = new Produto();
            p.setNomeProd(JsonUtil.getString(corpo, "nomeProd"));
            p.setPreco(JsonUtil.getBigDecimal(corpo, "preco"));
            Integer qtd = JsonUtil.getInt(corpo, "quantidade");
            p.setQuantidade(qtd == null ? 0 : qtd);
            p.setEntradas(qtd == null ? 0 : qtd);
            p.setSaidas(0);

            AppContext.PRODUTO_SERVICE.cadastrar(p);
            JsonUtil.escrever(resp, 201, paraJson(p));
        } catch (IllegalArgumentException e) {
            JsonUtil.erro(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.erro(resp, 500, "Erro ao cadastrar produto: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        Integer id = idDoCaminho(req.getPathInfo());
        if (id == null) { JsonUtil.erro(resp, 400, "Informe o ID do produto na URL."); return; }
        try {
            Optional<Produto> existenteOpt = AppContext.PRODUTO_SERVICE.buscarPorId(id);
            if (existenteOpt.isEmpty()) { JsonUtil.erro(resp, 404, "Produto não encontrado."); return; }

            Produto p = existenteOpt.get();
            JsonObject corpo = JsonUtil.lerCorpo(req);
            if (corpo.has("nomeProd"))   p.setNomeProd(JsonUtil.getString(corpo, "nomeProd"));
            if (corpo.has("preco"))      p.setPreco(JsonUtil.getBigDecimal(corpo, "preco"));
            if (corpo.has("quantidade")) p.setQuantidade(JsonUtil.getInt(corpo, "quantidade"));

            AppContext.PRODUTO_SERVICE.atualizar(p);
            JsonUtil.escrever(resp, 200, paraJson(p));
        } catch (IllegalArgumentException e) {
            JsonUtil.erro(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.erro(resp, 500, "Erro ao atualizar produto: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = idDoCaminho(req.getPathInfo());
        if (id == null) { JsonUtil.erro(resp, 400, "Informe o ID do produto na URL."); return; }
        try {
            AppContext.PRODUTO_SERVICE.excluir(id);
            JsonUtil.escrever(resp, 200, Map.of("mensagem", "Produto excluído com sucesso."));
        } catch (Exception e) {
            JsonUtil.erro(resp, 500, "Erro ao excluir produto: " + e.getMessage());
        }
    }

    private Integer idDoCaminho(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/")) return null;
        try { return Integer.valueOf(pathInfo.substring(1)); }
        catch (NumberFormatException e) { return null; }
    }

    private Map<String, Object> paraJson(Produto p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("nomeProd", p.getNomeProd());
        m.put("preco", p.getPreco());
        m.put("quantidade", p.getQuantidade());
        m.put("entradas", p.getEntradas());
        m.put("saidas", p.getSaidas());
        return m;
    }
}

package br.com.galeradacaneca.web.servlet;

import br.com.galeradacaneca.model.Cargo;
import br.com.galeradacaneca.model.Vendedor;
import br.com.galeradacaneca.web.AppContext;
import br.com.galeradacaneca.web.util.JsonUtil;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Endpoints REST para Vendedor/Gerente:
 *   GET    /api/vendedores?nome=       -> lista (com filtro opcional)
 *   GET    /api/vendedores/{id}        -> um funcionário
 *   POST   /api/vendedores             -> cadastra
 *   PUT    /api/vendedores/{id}        -> atualiza
 *   DELETE /api/vendedores/{id}        -> exclui
 */
@WebServlet(urlPatterns = {"/api/vendedores/*"})
public class VendedorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = idDoCaminho(req.getPathInfo());
        try {
            if (id == null) {
                String nome = req.getParameter("nome");
                List<Vendedor> lista = (nome == null || nome.isBlank())
                        ? AppContext.VENDEDOR_SERVICE.listarTodos()
                        : AppContext.VENDEDOR_SERVICE.pesquisarPorNome(nome);
                JsonUtil.escrever(resp, 200, lista.stream().map(this::paraJson).collect(Collectors.toList()));
            } else {
                Optional<Vendedor> v = AppContext.VENDEDOR_SERVICE.buscarPorId(id);
                if (v.isEmpty()) { JsonUtil.erro(resp, 404, "Funcionário não encontrado."); return; }
                JsonUtil.escrever(resp, 200, paraJson(v.get()));
            }
        } catch (Exception e) {
            JsonUtil.erro(resp, 500, "Erro ao consultar funcionários: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            JsonObject corpo = JsonUtil.lerCorpo(req);
            Vendedor v = new Vendedor();
            v.setNomeCompleto(JsonUtil.getString(corpo, "nomeCompleto"));
            v.setCpf(JsonUtil.getString(corpo, "cpf"));
            v.setNascimento(JsonUtil.getData(corpo, "nascimento"));
            v.setEmail(JsonUtil.getString(corpo, "email"));
            v.setSenha(JsonUtil.getString(corpo, "senha"));
            v.setSexo(JsonUtil.getString(corpo, "sexo"));
            v.setCargo(resolverCargo(Boolean.TRUE.equals(JsonUtil.getBoolean(corpo, "isGerente"))));

            AppContext.VENDEDOR_SERVICE.cadastrar(v);
            JsonUtil.escrever(resp, 201, paraJson(v));
        } catch (IllegalArgumentException e) {
            JsonUtil.erro(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.erro(resp, 500, "Erro ao cadastrar funcionário: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        Integer id = idDoCaminho(req.getPathInfo());
        if (id == null) { JsonUtil.erro(resp, 400, "Informe o ID do funcionário na URL."); return; }

        try {
            Optional<Vendedor> existenteOpt = AppContext.VENDEDOR_SERVICE.buscarPorId(id);
            if (existenteOpt.isEmpty()) { JsonUtil.erro(resp, 404, "Funcionário não encontrado."); return; }

            Vendedor v = existenteOpt.get();
            JsonObject corpo = JsonUtil.lerCorpo(req);

            if (corpo.has("nomeCompleto")) v.setNomeCompleto(JsonUtil.getString(corpo, "nomeCompleto"));
            if (corpo.has("cpf"))          v.setCpf(JsonUtil.getString(corpo, "cpf"));
            if (corpo.has("nascimento"))   v.setNascimento(JsonUtil.getData(corpo, "nascimento"));
            if (corpo.has("email"))        v.setEmail(JsonUtil.getString(corpo, "email"));
            if (corpo.has("sexo"))         v.setSexo(JsonUtil.getString(corpo, "sexo"));
            if (corpo.has("isGerente"))    v.setCargo(resolverCargo(Boolean.TRUE.equals(JsonUtil.getBoolean(corpo, "isGerente"))));

            String novaSenha = JsonUtil.getString(corpo, "senha");
            if (novaSenha != null && !novaSenha.isBlank()) v.setSenha(novaSenha);

            AppContext.VENDEDOR_SERVICE.atualizar(v);
            JsonUtil.escrever(resp, 200, paraJson(v));
        } catch (IllegalArgumentException e) {
            JsonUtil.erro(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.erro(resp, 500, "Erro ao atualizar funcionário: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = idDoCaminho(req.getPathInfo());
        if (id == null) { JsonUtil.erro(resp, 400, "Informe o ID do funcionário na URL."); return; }
        try {
            AppContext.VENDEDOR_SERVICE.excluir(id);
            JsonUtil.escrever(resp, 200, Map.of("mensagem", "Funcionário excluído com sucesso."));
        } catch (Exception e) {
            JsonUtil.erro(resp, 500, "Erro ao excluir funcionário: " + e.getMessage());
        }
    }

    // ── Auxiliares ───────────────────────────────────────────────────────────

    private Cargo resolverCargo(boolean gerente) {
        String descricao = gerente ? "Gerente" : "Vendedor";
        return AppContext.CARGO_DAO.buscarPorDescricao(descricao).orElseGet(() -> {
            Cargo novo = new Cargo(descricao);
            AppContext.CARGO_DAO.salvar(novo);
            return novo;
        });
    }

    private Integer idDoCaminho(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/")) return null;
        try { return Integer.valueOf(pathInfo.substring(1)); }
        catch (NumberFormatException e) { return null; }
    }

    private Map<String, Object> paraJson(Vendedor v) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", v.getId());
        m.put("nomeCompleto", v.getNomeCompleto());
        m.put("cpf", v.getCpf());
        LocalDate nasc = v.getNascimento();
        m.put("nascimento", nasc == null ? null : nasc.toString());
        m.put("email", v.getEmail());
        m.put("sexo", v.getSexo());
        m.put("isGerente", v.isGerente());
        m.put("cargo", v.getCargo() == null ? null : v.getCargo().getDescricao());
        return m;
    }
}

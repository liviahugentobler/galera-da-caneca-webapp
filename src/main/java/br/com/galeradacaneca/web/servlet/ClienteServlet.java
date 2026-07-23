package br.com.galeradacaneca.web.servlet;

import br.com.galeradacaneca.model.Cliente;
import br.com.galeradacaneca.web.AppContext;
import br.com.galeradacaneca.web.util.JsonUtil;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Endpoints REST para Cliente:
 *   GET    /api/clientes?nome=   -> lista (com filtro opcional)
 *   GET    /api/clientes/{id}    -> um cliente
 *   POST   /api/clientes         -> cadastra
 *   PUT    /api/clientes/{id}    -> atualiza
 *   DELETE /api/clientes/{id}    -> exclui
 */
@WebServlet(urlPatterns = {"/api/clientes/*"})
public class ClienteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = idDoCaminho(req.getPathInfo());
        try {
            if (id == null) {
                String nome = req.getParameter("nome");
                List<Cliente> lista = (nome == null || nome.isBlank())
                        ? AppContext.CLIENTE_SERVICE.listarTodos()
                        : AppContext.CLIENTE_SERVICE.pesquisarPorNome(nome);
                JsonUtil.escrever(resp, 200, lista.stream().map(this::paraJson).collect(Collectors.toList()));
            } else {
                Optional<Cliente> c = AppContext.CLIENTE_SERVICE.buscarPorId(id);
                if (c.isEmpty()) { JsonUtil.erro(resp, 404, "Cliente não encontrado."); return; }
                JsonUtil.escrever(resp, 200, paraJson(c.get()));
            }
        } catch (Exception e) {
            JsonUtil.erro(resp, 500, "Erro ao consultar clientes: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            JsonObject corpo = JsonUtil.lerCorpo(req);
            Cliente c = new Cliente();
            c.setNomeCompleto(JsonUtil.getString(corpo, "nomeCompleto"));
            c.setCpf(JsonUtil.getString(corpo, "cpf"));
            c.setNascimento(JsonUtil.getData(corpo, "nascimento"));
            c.setTelefone(JsonUtil.getString(corpo, "telefone"));
            c.setEmail(JsonUtil.getString(corpo, "email"));
            c.setSenha(JsonUtil.getString(corpo, "senha"));
            c.setEndereco(JsonUtil.getString(corpo, "endereco"));
            c.setSexo(JsonUtil.getString(corpo, "sexo"));

            AppContext.CLIENTE_SERVICE.cadastrar(c);
            JsonUtil.escrever(resp, 201, paraJson(c));
        } catch (IllegalArgumentException e) {
            JsonUtil.erro(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.erro(resp, 500, "Erro ao cadastrar cliente: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        Integer id = idDoCaminho(req.getPathInfo());
        if (id == null) { JsonUtil.erro(resp, 400, "Informe o ID do cliente na URL."); return; }
        try {
            Optional<Cliente> existenteOpt = AppContext.CLIENTE_SERVICE.buscarPorId(id);
            if (existenteOpt.isEmpty()) { JsonUtil.erro(resp, 404, "Cliente não encontrado."); return; }

            Cliente c = existenteOpt.get();
            JsonObject corpo = JsonUtil.lerCorpo(req);
            if (corpo.has("nomeCompleto")) c.setNomeCompleto(JsonUtil.getString(corpo, "nomeCompleto"));
            if (corpo.has("cpf"))          c.setCpf(JsonUtil.getString(corpo, "cpf"));
            if (corpo.has("nascimento"))   c.setNascimento(JsonUtil.getData(corpo, "nascimento"));
            if (corpo.has("telefone"))     c.setTelefone(JsonUtil.getString(corpo, "telefone"));
            if (corpo.has("email"))        c.setEmail(JsonUtil.getString(corpo, "email"));
            if (corpo.has("endereco"))     c.setEndereco(JsonUtil.getString(corpo, "endereco"));
            if (corpo.has("sexo"))         c.setSexo(JsonUtil.getString(corpo, "sexo"));

            String novaSenha = JsonUtil.getString(corpo, "senha");
            if (novaSenha != null && !novaSenha.isBlank()) c.setSenha(novaSenha);

            AppContext.CLIENTE_SERVICE.atualizar(c);
            JsonUtil.escrever(resp, 200, paraJson(c));
        } catch (IllegalArgumentException e) {
            JsonUtil.erro(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.erro(resp, 500, "Erro ao atualizar cliente: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = idDoCaminho(req.getPathInfo());
        if (id == null) { JsonUtil.erro(resp, 400, "Informe o ID do cliente na URL."); return; }
        try {
            AppContext.CLIENTE_SERVICE.excluir(id);
            JsonUtil.escrever(resp, 200, Map.of("mensagem", "Cliente excluído com sucesso."));
        } catch (Exception e) {
            JsonUtil.erro(resp, 500, "Erro ao excluir cliente: " + e.getMessage());
        }
    }

    private Integer idDoCaminho(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/")) return null;
        try { return Integer.valueOf(pathInfo.substring(1)); }
        catch (NumberFormatException e) { return null; }
    }

    private Map<String, Object> paraJson(Cliente c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", c.getId());
        m.put("nomeCompleto", c.getNomeCompleto());
        m.put("cpf", c.getCpf());
        LocalDate nasc = c.getNascimento();
        m.put("nascimento", nasc == null ? null : nasc.toString());
        m.put("telefone", c.getTelefone());
        m.put("email", c.getEmail());
        m.put("endereco", c.getEndereco());
        m.put("sexo", c.getSexo());
        return m;
    }
}

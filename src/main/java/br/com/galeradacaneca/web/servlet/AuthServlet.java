package br.com.galeradacaneca.web.servlet;

import br.com.galeradacaneca.model.Vendedor;
import br.com.galeradacaneca.web.AppContext;
import br.com.galeradacaneca.web.util.JsonUtil;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Endpoint de autenticação: POST /api/auth/login
 * Corpo esperado: { "email": "...", "senha": "..." }
 */
@WebServlet(urlPatterns = {"/api/auth/*"})
public class AuthServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String caminho = req.getPathInfo();

        if (caminho == null || !caminho.equals("/login")) {
            JsonUtil.erro(resp, 404, "Rota de autenticação não encontrada.");
            return;
        }

        try {
            JsonObject corpo = JsonUtil.lerCorpo(req);
            String email = JsonUtil.getString(corpo, "email");
            String senha = JsonUtil.getString(corpo, "senha");

            if (email == null || senha == null) {
                JsonUtil.erro(resp, 400, "Informe e-mail e senha.");
                return;
            }

            Optional<Vendedor> vendedor = AppContext.VENDEDOR_SERVICE.autenticar(email, senha);
            if (vendedor.isEmpty()) {
                JsonUtil.erro(resp, 401, "E-mail ou senha inválidos.");
                return;
            }

            Vendedor v = vendedor.get();
            Map<String, Object> corpoResposta = new LinkedHashMap<>();
            corpoResposta.put("id", v.getId());
            corpoResposta.put("nomeCompleto", v.getNomeCompleto());
            corpoResposta.put("email", v.getEmail());
            corpoResposta.put("isGerente", v.isGerente());
            JsonUtil.escrever(resp, 200, corpoResposta);

        } catch (Exception e) {
            JsonUtil.erro(resp, 500, "Erro ao autenticar: " + e.getMessage());
        }
    }
}

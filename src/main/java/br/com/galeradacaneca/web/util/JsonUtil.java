package br.com.galeradacaneca.web.util;

import com.google.gson.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utilitário central de JSON para os servlets da API REST.
 *
 * SOLID — SRP: responsabilidade única de ler/escrever JSON no protocolo HTTP,
 * isolando os servlets de detalhes de serialização.
 */
public final class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, type, ctx) ->
            src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString()))
        .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, ctx) ->
            (json == null || json.isJsonNull() || json.getAsString().isBlank()) ? null : LocalDate.parse(json.getAsString()))
        .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, type, ctx) ->
            src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString()))
        .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, ctx) ->
            (json == null || json.isJsonNull() || json.getAsString().isBlank()) ? null : LocalDateTime.parse(json.getAsString()))
        .create();

    private JsonUtil() {}

    /** Lê o corpo da requisição e devolve como JsonObject (vazio se não houver corpo). */
    public static JsonObject lerCorpo(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String linha;
            while ((linha = reader.readLine()) != null) sb.append(linha);
        }
        if (sb.length() == 0) return new JsonObject();
        JsonElement el = JsonParser.parseString(sb.toString());
        return el.isJsonObject() ? el.getAsJsonObject() : new JsonObject();
    }

    /** Escreve um objeto qualquer como JSON na resposta, com o status HTTP informado. */
    public static void escrever(HttpServletResponse resp, int status, Object corpo) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(GSON.toJson(corpo));
    }

    /** Atalho para respostas de erro no formato { "erro": "mensagem" }. */
    public static void erro(HttpServletResponse resp, int status, String mensagem) throws IOException {
        Map<String, String> corpo = new LinkedHashMap<>();
        corpo.put("erro", mensagem);
        escrever(resp, status, corpo);
    }

    // ── Leitura tolerante de campos do JsonObject de entrada ───────────────────

    public static String getString(JsonObject obj, String campo) {
        return (obj.has(campo) && !obj.get(campo).isJsonNull()) ? obj.get(campo).getAsString().trim() : null;
    }

    public static Integer getInt(JsonObject obj, String campo) {
        return (obj.has(campo) && !obj.get(campo).isJsonNull()) ? obj.get(campo).getAsInt() : null;
    }

    public static Boolean getBoolean(JsonObject obj, String campo) {
        return (obj.has(campo) && !obj.get(campo).isJsonNull()) && obj.get(campo).getAsBoolean();
    }

    public static BigDecimal getBigDecimal(JsonObject obj, String campo) {
        return (obj.has(campo) && !obj.get(campo).isJsonNull()) ? obj.get(campo).getAsBigDecimal() : null;
    }

    public static LocalDate getData(JsonObject obj, String campo) {
        String valor = getString(obj, campo);
        return (valor == null || valor.isBlank()) ? null : LocalDate.parse(valor);
    }
}

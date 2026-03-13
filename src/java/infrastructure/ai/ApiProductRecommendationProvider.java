package infrastructure.ai;

import application.ports.IProductRecommendationAI;
import domain.entities.Product;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * API-based product recommender.
 *
 * Contract (expected response JSON):
 * { "product_ids": ["PROP-AAAA1111", "PROP-BBBB2222"] }
 *
 * Request payload:
 * {
 *   "user_id": "...",
 *   "limit": 3,
 *   "candidates": [{"id":"...","name":"...","category":"...","description":"..."}]
 * }
 *
 * Configure via environment variables:
 * - AI_RECOMMEND_URL (required)
 * - AI_API_KEY (optional, sent as Bearer token)
 * - AI_API_TIMEOUT_MS (optional)
 */
public class ApiProductRecommendationProvider implements IProductRecommendationAI {
    private final URL url;
    private final String apiKey;
    private final int timeoutMs;

    public ApiProductRecommendationProvider(String apiUrl, String apiKey, int timeoutMs) {
        if (apiUrl == null || apiUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("AI_RECOMMEND_URL is required for ApiProductRecommendationProvider");
        }
        try {
            this.url = new URL(apiUrl.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid AI_RECOMMEND_URL: " + apiUrl, e);
        }
        this.apiKey = (apiKey == null) ? "" : apiKey.trim();
        this.timeoutMs = (timeoutMs <= 0) ? 10_000 : timeoutMs;
    }

    @Override
    public List<String> recommendProductIds(String userId, List<Product> candidates, int limit) throws Exception {
        if (limit <= 0) limit = 3;
        if (candidates == null) candidates = new ArrayList<>();

        String safeUser = (userId == null) ? "" : userId.replace("\\", "\\\\").replace("\"", "\\\"");

        StringBuilder cand = new StringBuilder();
        cand.append("[");
        for (int i = 0; i < candidates.size(); i++) {
            Product p = candidates.get(i);
            if (p == null || p.getProductId() == null) continue;

            String id = esc(p.getProductId());
            String name = esc(nvl(p.getName()));
            String category = esc(nvl(p.getCategory()));
            String desc = esc(trunc(nvl(p.getDescription()), 220));

            if (cand.length() > 1) cand.append(",");
            cand.append("{")
                    .append("\"id\":\"").append(id).append("\",")
                    .append("\"name\":\"").append(name).append("\",")
                    .append("\"category\":\"").append(category).append("\",")
                    .append("\"description\":\"").append(desc).append("\"")
                    .append("}");
        }
        cand.append("]");

        String payload = "{"
                + "\"user_id\":\"" + safeUser + "\","
                + "\"limit\":" + limit + ","
                + "\"candidates\":" + cand
                + "}";

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(timeoutMs);
        conn.setReadTimeout(timeoutMs);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        if (!apiKey.isEmpty()) {
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        }
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes("UTF-8"));
        }

        int status = conn.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                status >= 200 && status < 300 ? conn.getInputStream() : conn.getErrorStream(),
                "UTF-8"
        ));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        String body = sb.toString();
        if (status < 200 || status >= 300) {
            throw new IllegalStateException("AI recommend API error: HTTP " + status + " body=" + body);
        }

        Map<String, Object> obj = JsonCompat.parseObject(body);
        Object ids = obj.get("product_ids");
        List<String> out = new ArrayList<>();
        if (ids instanceof List) {
            for (Object item : (List<?>) ids) {
                if (item != null) out.add(String.valueOf(item));
            }
        }
        return out;
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }

    private static String trunc(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max);
    }

    private static String esc(String s) {
        return (s == null) ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}


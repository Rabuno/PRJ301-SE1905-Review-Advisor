package infrastructure.ai;

import application.dto.AiTriageResult;
import application.ports.IReviewTriageAI;
import domain.entities.Review;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * API-based AI triage provider.
 *
 * Contract (expected response JSON):
 * {
 *   "risk_score": 0.0..1.0,
 *   "labels": ["spam", "fake_review", "impersonation", "policy_violation"],
 *   "reasons": [{"feature":"...", "weight":0.0..1.0, "description":"..."}]
 * }
 *
 * Configure via environment variables:
 * - AI_API_URL (required)
 * - AI_API_KEY (optional, sent as Bearer token)
 * - AI_API_TIMEOUT_MS (optional)
 */
public class ApiReviewAiProvider implements IReviewTriageAI {

    private final URL url;
    private final String apiKey;
    private final int timeoutMs;

    public ApiReviewAiProvider(String apiUrl, String apiKey, int timeoutMs) {
        if (apiUrl == null || apiUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("AI_API_URL is required for ApiReviewAiProvider");
        }
        try {
            this.url = new URL(apiUrl.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid AI_API_URL: " + apiUrl, e);
        }
        this.apiKey = (apiKey == null) ? "" : apiKey.trim();
        this.timeoutMs = (timeoutMs <= 0) ? 10_000 : timeoutMs;
    }

    @Override
    public AiTriageResult analyzeReview(Review review, double accountAgeDays, double burstRate) throws Exception {
        String text = (review == null || review.getContent() == null) ? "" : review.getContent();
        int rating = (review == null) ? 0 : review.getRating();

        // Minimal JSON without a library (escape only backslash and quotes).
        String safeText = text.replace("\\", "\\\\").replace("\"", "\\\"");
        String payload = "{"
                + "\"review_text\":\"" + safeText + "\","
                + "\"rating\":" + rating + ","
                + "\"account_age_days\":" + accountAgeDays + ","
                + "\"burst_rate\":" + burstRate
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
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String body = sb.toString();

        if (status < 200 || status >= 300) {
            throw new IllegalStateException("AI API error: HTTP " + status + " body=" + body);
        }

        Map<String, Object> obj = JsonCompat.parseObject(body);

        double risk = readDouble(obj, "risk_score", readDouble(obj, "riskScore", 0.0));
        Set<String> labels = readStringSet(obj, "labels");
        List<AiTriageResult.Reason> reasons = readReasons(obj, "reasons");

        return new AiTriageResult(risk, labels, reasons);
    }

    private static double readDouble(Map<String, Object> obj, String key, double fallback) {
        Object v = obj.get(key);
        if (v instanceof Number) return ((Number) v).doubleValue();
        if (v instanceof String) {
            try {
                return Double.parseDouble(((String) v).trim());
            } catch (Exception ignored) {
            }
        }
        return fallback;
    }

    @SuppressWarnings("unchecked")
    private static Set<String> readStringSet(Map<String, Object> obj, String key) {
        Object v = obj.get(key);
        Set<String> out = new HashSet<>();
        if (v instanceof List) {
            for (Object item : (List<Object>) v) {
                if (item != null) out.add(String.valueOf(item));
            }
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    private static List<AiTriageResult.Reason> readReasons(Map<String, Object> obj, String key) {
        Object v = obj.get(key);
        List<AiTriageResult.Reason> out = new ArrayList<>();
        if (!(v instanceof List)) return out;

        for (Object item : (List<Object>) v) {
            if (!(item instanceof Map)) continue;
            Map<String, Object> m = (Map<String, Object>) item;
            String feature = String.valueOf(m.getOrDefault("feature", "ai_reason"));
            double weight = 0.25;
            Object w = m.get("weight");
            if (w instanceof Number) weight = ((Number) w).doubleValue();
            String desc = String.valueOf(m.getOrDefault("description", ""));
            out.add(new AiTriageResult.Reason(feature, weight, desc));
        }
        return out;
    }
}

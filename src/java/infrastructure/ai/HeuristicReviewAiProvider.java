package infrastructure.ai;

import application.dto.AiTriageResult;
import application.ports.IReviewTriageAI;
import domain.entities.Review;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Lightweight, no-network AI triage provider.
 *
 * This is not a "true ML model", but provides practical signals:
 * - spam/advertising patterns
 * - impersonation / fake claims
 * - basic standards/policy violations (profanity / hate / harassment keywords)
 *
 * This keeps the app running even when an external AI API is not configured.
 */
public class HeuristicReviewAiProvider implements IReviewTriageAI {

    private static final Pattern URL = Pattern.compile("(https?://|www\\.)\\S+", Pattern.CASE_INSENSITIVE);
    private static final Pattern EMAIL = Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", Pattern.CASE_INSENSITIVE);
    private static final Pattern PHONE = Pattern.compile("(\\+?\\d[\\d\\s\\-().]{7,}\\d)");
    private static final Pattern MANY_EXCL = Pattern.compile("!{3,}");
    private static final Pattern REPEAT_CHAR = Pattern.compile("(.)\\1{5,}");

    // Keep this list short and safe; expand per your policy needs.
    private static final String[] PROFANITY = new String[] {
            "fuck", "shit", "bitch", "asshole"
    };

    private static final String[] IMPERSONATION = new String[] {
            "i am the owner", "i'm the owner", "official", "verified", "admin", "support team"
    };

    private static final String[] SPAMMY = new String[] {
            "buy now", "discount", "promo", "sale", "best price", "click", "subscribe"
    };

    @Override
    public AiTriageResult analyzeReview(Review review, double accountAgeDays, double burstRate) {
        String text = (review == null || review.getContent() == null) ? "" : review.getContent().trim();
        String lower = text.toLowerCase();

        double score = 0.0;
        Set<String> labels = new HashSet<>();
        List<AiTriageResult.Reason> reasons = new ArrayList<>();

        // Low-quality / suspicious behavior
        if (text.length() < 15) {
            score += 0.10;
            reasons.add(new AiTriageResult.Reason("low_content", 0.10, "Nội dung quá ngắn, ít thông tin."));
        }

        if (accountAgeDays >= 0 && accountAgeDays < 3) {
            score += 0.15;
            labels.add("fake_review");
            reasons.add(new AiTriageResult.Reason("new_account", 0.15, "Tài khoản mới tạo (rủi ro giả mạo cao hơn)."));
        }

        if (burstRate >= 5.0) {
            score += 0.20;
            labels.add("spam");
            reasons.add(new AiTriageResult.Reason("burst_rate", 0.20, "Tần suất review bất thường trong thời gian ngắn (nghi spam)."));
        }

        // Spam / solicitation signals
        if (URL.matcher(lower).find()) {
            score += 0.25;
            labels.add("spam");
            reasons.add(new AiTriageResult.Reason("external_link", 0.25, "Chứa đường dẫn bên ngoài (dấu hiệu quảng cáo/spam)."));
        }
        if (EMAIL.matcher(lower).find() || PHONE.matcher(lower).find()) {
            score += 0.25;
            labels.add("policy_violation");
            reasons.add(new AiTriageResult.Reason("contact_info", 0.25, "Có thể chứa thông tin liên hệ (vi phạm tiêu chuẩn)."));
        }
        if (MANY_EXCL.matcher(text).find() || REPEAT_CHAR.matcher(text).find()) {
            score += 0.10;
            labels.add("spam");
            reasons.add(new AiTriageResult.Reason("excessive_emphasis", 0.10, "Dấu hiệu nhấn mạnh quá mức (spammy)."));
        }

        for (String k : SPAMMY) {
            if (lower.contains(k)) {
                score += 0.15;
                labels.add("spam");
                reasons.add(new AiTriageResult.Reason("spam_keyword", 0.15, "Có cụm từ mang tính quảng cáo/spam."));
                break;
            }
        }

        // Impersonation / fake claims
        for (String k : IMPERSONATION) {
            if (lower.contains(k)) {
                score += 0.20;
                labels.add("impersonation");
                reasons.add(new AiTriageResult.Reason("impersonation_claim", 0.20, "Có dấu hiệu mạo danh/nhận là đại diện chính thức."));
                break;
            }
        }

        // Basic policy / standards violation
        for (String bad : PROFANITY) {
            if (lower.contains(bad)) {
                score += 0.35;
                labels.add("policy_violation");
                reasons.add(new AiTriageResult.Reason("profanity", 0.35, "Ngôn từ thô tục/công kích (vi phạm tiêu chuẩn)."));
                break;
            }
        }

        // Rating-content mismatch heuristic (very rough)
        if (review != null && review.getRating() == 5 && lower.contains("terrible")) {
            score += 0.10;
            labels.add("fake_review");
            reasons.add(new AiTriageResult.Reason("rating_mismatch", 0.10, "Nội dung và số sao có dấu hiệu mâu thuẫn."));
        }

        return new AiTriageResult(score, labels, reasons);
    }
}


package infrastructure.ai;

import application.dto.AiTriageResult;
import application.ports.IProductTriageAI;
import domain.entities.Product;
import domain.entities.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Heuristic product/listing triage (no network).
 */
public class HeuristicProductAiProvider implements IProductTriageAI {
    private static final Pattern URL = Pattern.compile("(https?://|www\\.)\\S+", Pattern.CASE_INSENSITIVE);
    private static final Pattern EMAIL = Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", Pattern.CASE_INSENSITIVE);
    private static final Pattern PHONE = Pattern.compile("(\\+?\\d[\\d\\s\\-().]{7,}\\d)");

    private static final String[] SPAMMY = new String[] {
            "buy now", "discount", "promo", "sale", "best price", "click", "subscribe", "ctv", "cọc", "zalo"
    };

    @Override
    public AiTriageResult analyzeProduct(Product product, User merchant) {
        String name = product == null ? "" : nvl(product.getName());
        String category = product == null ? "" : nvl(product.getCategory());
        String desc = product == null ? "" : nvl(product.getDescription());
        String text = (name + " " + category + " " + desc).trim();
        String lower = text.toLowerCase();

        double score = 0.0;
        Set<String> labels = new HashSet<>();
        List<AiTriageResult.Reason> reasons = new ArrayList<>();

        if (name.trim().length() < 3) {
            score += 0.20;
            labels.add("policy_violation");
            reasons.add(new AiTriageResult.Reason("low_quality_name", 0.20, "Tên listing quá ngắn/thiếu thông tin."));
        }
        if (desc.trim().length() < 30) {
            score += 0.10;
            reasons.add(new AiTriageResult.Reason("low_quality_description", 0.10, "Mô tả quá ngắn, khó xác minh."));
        }

        if (URL.matcher(lower).find()) {
            score += 0.35;
            labels.add("spam");
            reasons.add(new AiTriageResult.Reason("external_link", 0.35, "Chứa link bên ngoài (dấu hiệu spam)."));
        }
        if (EMAIL.matcher(lower).find() || PHONE.matcher(lower).find()) {
            score += 0.35;
            labels.add("policy_violation");
            reasons.add(new AiTriageResult.Reason("contact_info", 0.35, "Chứa thông tin liên hệ (không phù hợp trong listing)."));
        }

        for (String k : SPAMMY) {
            if (lower.contains(k)) {
                score += 0.20;
                labels.add("spam");
                reasons.add(new AiTriageResult.Reason("spam_keyword", 0.20, "Có từ khóa quảng cáo/spam trong listing."));
                break;
            }
        }

        // Light account signal: very new merchant account => more scrutiny.
        if (merchant != null && merchant.getCreatedAt() != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(
                    merchant.getCreatedAt().toLocalDate(),
                    java.time.LocalDate.now()
            );
            if (days >= 0 && days < 7) {
                score += 0.10;
                labels.add("fake_listing");
                reasons.add(new AiTriageResult.Reason("new_merchant_account", 0.10, "Merchant account mới tạo, cần kiểm tra kỹ."));
            }
        }

        return new AiTriageResult(score, labels, reasons);
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }
}


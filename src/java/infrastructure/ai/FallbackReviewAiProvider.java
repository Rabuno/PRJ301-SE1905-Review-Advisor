package infrastructure.ai;

import application.dto.AiTriageResult;
import application.ports.IReviewTriageAI;
import domain.entities.Review;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tries primary AI first, falls back to secondary when primary fails.
 */
public class FallbackReviewAiProvider implements IReviewTriageAI {
    private final IReviewTriageAI primary;
    private final IReviewTriageAI secondary;

    public FallbackReviewAiProvider(IReviewTriageAI primary, IReviewTriageAI secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    @Override
    public AiTriageResult analyzeReview(Review review, double accountAgeDays, double burstRate) throws Exception {
        try {
            return primary.analyzeReview(review, accountAgeDays, burstRate);
        } catch (Exception e) {
            AiTriageResult fallback = secondary.analyzeReview(review, accountAgeDays, burstRate);
            // Add a small reason so moderators know it was a fallback decision.
            Set<String> labels = new HashSet<>(fallback.getLabels());
            List<AiTriageResult.Reason> reasons = new ArrayList<>(fallback.getReasons());
            reasons.add(new AiTriageResult.Reason("ai_fallback", 0.05, "AI API lỗi, hệ thống dùng heuristic fallback."));
            return new AiTriageResult(fallback.getRiskScore(), labels, reasons);
        }
    }
}


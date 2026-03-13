package application.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Result from AI triage of a review.
 *
 * riskScore: 0..1 (higher => more suspicious)
 * labels: e.g. spam, fake_review, impersonation, policy_violation
 * reasons: human-readable explanation for moderators.
 */
public class AiTriageResult {
    private final double riskScore;
    private final Set<String> labels;
    private final List<Reason> reasons;

    public AiTriageResult(double riskScore, Set<String> labels, List<Reason> reasons) {
        this.riskScore = clamp01(riskScore);
        this.labels = (labels == null) ? new HashSet<>() : new HashSet<>(labels);
        this.reasons = (reasons == null) ? new ArrayList<>() : new ArrayList<>(reasons);
    }

    public double getRiskScore() {
        return riskScore;
    }

    public Set<String> getLabels() {
        return Collections.unmodifiableSet(labels);
    }

    public List<Reason> getReasons() {
        return Collections.unmodifiableList(reasons);
    }

    public static class Reason {
        private final String feature;
        private final double weight; // 0..1 (relative importance)
        private final String description;

        public Reason(String feature, double weight, String description) {
            this.feature = feature;
            this.weight = clamp01(weight);
            this.description = description;
        }

        public String getFeature() {
            return feature;
        }

        public double getWeight() {
            return weight;
        }

        public String getDescription() {
            return description;
        }
    }

    private static double clamp01(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return 0.0;
        if (v < 0.0) return 0.0;
        if (v > 1.0) return 1.0;
        return v;
    }
}


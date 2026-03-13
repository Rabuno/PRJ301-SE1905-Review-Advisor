package application.services;

import application.dto.AiTriageResult;
import application.ports.IProductTriageAI;
import domain.entities.Product;
import domain.entities.User;

/**
 * Runs AI triage on product/listing submissions.
 *
 * In this project, products are never auto-activated by merchants.
 * They are set to PENDING and require moderator/admin approval after AI scan.
 */
public class ProductTriageService {
    private final IProductTriageAI ai;

    // Similar threshold to review triage, but slightly lower for safety.
    private static final double RISK_THRESHOLD = 0.65;

    public ProductTriageService(IProductTriageAI ai) {
        this.ai = ai;
    }

    public AiTriageResult evaluate(Product product, User merchant) throws Exception {
        AiTriageResult r = ai.analyzeProduct(product, merchant);
        return r == null ? new AiTriageResult(0.0, null, null) : r;
    }

    public boolean isFlagged(AiTriageResult r) {
        if (r == null) return false;
        if (r.getRiskScore() >= RISK_THRESHOLD) return true;
        return r.getLabels().contains("policy_violation")
                || r.getLabels().contains("spam")
                || r.getLabels().contains("impersonation");
    }
}


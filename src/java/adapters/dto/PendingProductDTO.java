package adapters.dto;

import application.dto.AiTriageResult;
import domain.entities.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * DTO for product moderation: Product + AI triage result.
 */
public class PendingProductDTO {
    private final Product product;
    private final AiTriageResult triage;

    public PendingProductDTO(Product product, AiTriageResult triage) {
        this.product = product;
        this.triage = triage;
    }

    public Product getProduct() {
        return product;
    }

    public AiTriageResult getTriage() {
        return triage;
    }

    public String getRiskScoreDisplay() {
        if (triage == null) return "N/A";
        return String.format("%.2f", triage.getRiskScore());
    }

    public List<String> getLabels() {
        if (triage == null) return new ArrayList<>();
        Set<String> labels = triage.getLabels();
        return labels == null ? new ArrayList<>() : new ArrayList<>(labels);
    }

    public List<AiTriageResult.Reason> getReasons() {
        if (triage == null) return new ArrayList<>();
        return new ArrayList<>(triage.getReasons());
    }
}


package application.ports;

import application.dto.AiTriageResult;
import domain.entities.Product;
import domain.entities.User;

/**
 * Port for AI triage (moderation) of products/listings.
 */
public interface IProductTriageAI {
    AiTriageResult analyzeProduct(Product product, User merchant) throws Exception;
}


package application.ports;

import domain.entities.Product;
import java.util.List;

/**
 * Port for AI-based product recommendations.
 *
 * Implementations can call an external API and return productIds (ranked).
 */
public interface IProductRecommendationAI {
    List<String> recommendProductIds(String userId, List<Product> candidates, int limit) throws Exception;
}


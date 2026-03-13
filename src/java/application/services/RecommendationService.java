package application.services;

import application.dto.ProductReviewStatsDTO;
import application.ports.IProductRecommendationAI;
import domain.entities.Product;
import domain.entities.Review;
import domain.entities.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Product recommendations (API-first, fallback to top-rated).
 */
public class RecommendationService {
    private final IProductRecommendationAI ai;
    private final ReviewService reviewService;

    public RecommendationService(IProductRecommendationAI ai, ReviewService reviewService) {
        this.ai = ai;
        this.reviewService = reviewService;
    }

    public List<Product> recommendForUser(User user, List<Product> candidates, int limit) {
        if (candidates == null || candidates.isEmpty() || limit <= 0) return Collections.emptyList();

        // 1) Try AI API (if configured)
        if (ai != null) {
            try {
                String userId = (user == null) ? "" : user.getUserId();
                List<String> ids = ai.recommendProductIds(userId, candidates, limit);
                if (ids != null && !ids.isEmpty()) {
                    Map<String, Product> byId = new HashMap<>();
                    for (Product p : candidates) {
                        if (p != null && p.getProductId() != null) byId.put(p.getProductId(), p);
                    }
                    List<Product> out = new ArrayList<>();
                    for (String id : ids) {
                        Product p = byId.get(id);
                        if (p != null) out.add(p);
                        if (out.size() >= limit) break;
                    }
                    if (!out.isEmpty()) return out;
                }
            } catch (Exception ignored) {
                // Fall back below
            }
        }

        // 2) Fallback: pick "potential" products by rating + volume
        List<ScoredProduct> scored = new ArrayList<>();
        for (Product p : candidates) {
            if (p == null || p.getProductId() == null) continue;
            List<Review> reviews = reviewService.getReviewsByProduct(p.getProductId());
            ProductReviewStatsDTO stats = new ProductReviewStatsDTO(reviews);
            double avg = stats.getAverageRating();
            int count = stats.getTotalReviews();
            // Slightly favor products with more reviews, but keep avg rating dominant.
            double score = avg + Math.min(0.5, Math.log10(count + 1) * 0.15);
            scored.add(new ScoredProduct(p, score));
        }

        scored.sort(Comparator.comparingDouble(ScoredProduct::getScore).reversed());
        List<Product> out = new ArrayList<>();
        for (ScoredProduct sp : scored) {
            out.add(sp.product);
            if (out.size() >= limit) break;
        }
        return out;
    }

    private static class ScoredProduct {
        private final Product product;
        private final double score;

        private ScoredProduct(Product product, double score) {
            this.product = product;
            this.score = score;
        }

        private double getScore() {
            return score;
        }
    }
}


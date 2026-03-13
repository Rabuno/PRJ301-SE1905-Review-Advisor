package application.ports;

import application.dto.AiTriageResult;
import domain.entities.Review;

/**
 * Port for AI triage (moderation) of user reviews.
 *
 * Implementations can be heuristic (no network) or API-based (LLM / moderation service).
 */
public interface IReviewTriageAI {
    AiTriageResult analyzeReview(Review review, double accountAgeDays, double burstRate) throws Exception;
}


package application.ports;

import domain.entities.Review;

public interface IAIService {
    // Trả về Risk Score (0.0 - 1.0) dự đoán review giả mạo
    double calculateRiskScore(Review review);
}

package application.dto;

import domain.entities.Review;
import java.util.Locale;
import java.util.List;

public class ProductReviewStatsDTO {

    private int totalReviews;
    private double averageRating;
    private int[] starCounts;

    public ProductReviewStatsDTO(List<Review> reviews) {
        this.starCounts = new int[6]; // Bỏ qua index 0, dùng index 1-5 cho số sao
        this.totalReviews = 0;
        this.averageRating = 0.0;

        if (reviews != null && !reviews.isEmpty()) {
            this.totalReviews = reviews.size();
            double totalStars = 0;
            for (Review r : reviews) {
                totalStars += r.getRating();
                if (r.getRating() >= 1 && r.getRating() <= 5) {
                    this.starCounts[r.getRating()]++;
                }
            }
            this.averageRating = totalStars / this.totalReviews;
        }
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public String getAverageRatingFormatted() {
        // Keep decimal separator stable (.) regardless of server default locale.
        return String.format(Locale.US, "%.1f", averageRating);
    }

    // Raw value is useful for rendering stars (comparisons) without string coercion.
    public double getAverageRating() {
        return averageRating;
    }

    public int[] getStarCounts() {
        return starCounts;
    }
}

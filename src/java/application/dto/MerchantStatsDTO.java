package application.dto;

public class MerchantStatsDTO {
    private int totalProperties;
    private double avgRating;
    private int publishedCount;
    private int flaggedCount;

    public MerchantStatsDTO(int totalProperties, double avgRating, int publishedCount, int flaggedCount) {
        this.totalProperties = totalProperties;
        this.avgRating = avgRating;
        this.publishedCount = publishedCount;
        this.flaggedCount = flaggedCount;
    }

    public int getTotalProperties() {
        return totalProperties;
    }

    public void setTotalProperties(int totalProperties) {
        this.totalProperties = totalProperties;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public int getPublishedCount() {
        return publishedCount;
    }

    public void setPublishedCount(int publishedCount) {
        this.publishedCount = publishedCount;
    }

    public int getFlaggedCount() {
        return flaggedCount;
    }

    public void setFlaggedCount(int flaggedCount) {
        this.flaggedCount = flaggedCount;
    }
}

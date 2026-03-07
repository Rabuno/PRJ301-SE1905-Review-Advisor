package domain.entities;

public class Product {
    private String productId;
    private String name;
    private String description;
    private double price;
    private String merchantId;

    public Product(String productId, String name, String description, double price, String merchantId) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.merchantId = merchantId;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getMerchantId() {
        return merchantId;
    }

    // Stub methods for UI compatibility (future updates)
    public String getCategory() {
        return "";
    }

    public String getImageUrl() {
        return "";
    }
}
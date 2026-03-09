package domain.entities;

public class Product {
    private String productId;
    private String name;
    private String description;
    private double price;
    private String category;
    private String status;
    private String merchantId;
    private String imageUrl;

    public Product(String productId, String name, String description, double price, String category, String status,
            String merchantId, String imageUrl) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.status = status;
        this.merchantId = merchantId;
        this.imageUrl = imageUrl;
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

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
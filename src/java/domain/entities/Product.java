package domain.entities;

import domain.enums.Status;

public class Product {
    private String productId;
    private String name;
    private String category;
    private String description;
    private double price;
    private Status status;
    private String merchantId;
    private String imageUrl;

    public Product(String productId, String name, String category, String description, double price, String merchantId,
            String imageUrl) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.status = Status.PENDING;
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

    public void setCategory(String category) {
        this.category = category;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
package application.services;

import application.ports.IProductRepository;
import domain.entities.Product;
import java.util.List;

public class ProductService {
    private final IProductRepository productRepository;

    public ProductService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll(); // Có thể tích hợp thêm logic lọc, phân trang ở đây
    }

    public Product getProductById(String productId) {
        return productRepository.findById(productId);
    }

    public int countPropertiesByMerchant(String merchantId) {
        return productRepository.countByMerchantId(merchantId);
    }

    public boolean addProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getProductsByMerchant(String merchantId) {
        return productRepository.findByMerchantId(merchantId);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public boolean updateProduct(Product product) {
        return productRepository.update(product);
    }
}
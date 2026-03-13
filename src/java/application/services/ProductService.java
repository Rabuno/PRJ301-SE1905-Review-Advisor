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
        return productRepository.findAll();
    }

    public Product getProductById(String productId) {
        return productRepository.findById(productId);
    }

    public List<Product> getProductsByMerchant(String merchantId) {
        return productRepository.findByMerchantId(merchantId);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public int countPropertiesByMerchant(String merchantId) {
        return productRepository.countByMerchantId(merchantId);
    }

    public boolean createProduct(Product product) {
        return productRepository.save(product);
    }

    public boolean updateProduct(Product product) {
        return productRepository.update(product);
    }
}
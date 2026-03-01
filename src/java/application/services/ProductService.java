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
}
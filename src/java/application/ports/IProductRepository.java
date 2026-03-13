package application.ports;

import domain.entities.Product;
import domain.enums.ProductStatus;
import java.util.List;

public interface IProductRepository {

    List<Product> findAll();

    List<Product> findByMerchantId(String merchantId);

    List<Product> findByCategory(String category);

    Product findById(String productId);

    // Used by moderation/admin flows (not filtered to ACTIVE).
    Product findByIdAnyStatus(String productId);

    List<Product> findByStatus(ProductStatus status);

    boolean updateStatus(String productId, ProductStatus status);
    
    List<Product> searchProducts(String keyword);

    boolean save(Product product);

    boolean update(Product product);

    int countByMerchantId(String merchantId);
}

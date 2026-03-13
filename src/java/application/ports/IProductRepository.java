package application.ports;

import domain.entities.Product;
import java.util.List;

public interface IProductRepository {

    List<Product> findAll();

    List<Product> findByMerchantId(String merchantId);

    List<Product> findByCategory(String category);

    Product findById(String productId);
    
    List<Product> searchProducts(String keyword);

    boolean save(Product product);

    boolean update(Product product);

    int countByMerchantId(String merchantId);
}

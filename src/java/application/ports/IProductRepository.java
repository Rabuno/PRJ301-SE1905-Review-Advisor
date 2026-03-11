package application.ports;

import domain.entities.Product;
import java.util.List;

public interface IProductRepository {
    List<Product> findAll();

    List<Product> findByMerchantId(String merchantId);

    List<Product> findByCategory(String category);

    Product findById(String productId);

    boolean save(Product product);

    boolean update(Product product);

    int countByMerchantId(String merchantId);

    List<Product> findByMerchantId(String merchantId);

    boolean save(Product product);

    boolean update(Product product);
}
package application.ports;

import domain.entities.Product;
import java.util.List;

public interface IProductRepository {
    List<Product> findAll();

    Product findById(String productId);

    int countByMerchantId(String merchantId);

    List<Product> findByMerchantId(String merchantId);

    boolean save(Product product);

    boolean update(Product product);
}
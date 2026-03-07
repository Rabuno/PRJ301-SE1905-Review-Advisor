package application.ports;

import domain.entities.Product;
import java.util.List;

public interface IProductRepository {
    List<Product> findAll();

    Product findById(String productId);
}
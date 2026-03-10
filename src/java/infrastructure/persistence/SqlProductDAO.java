package infrastructure.persistence;

import application.ports.IProductRepository;
import domain.entities.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SqlProductDAO implements IProductRepository {

    @Override
    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT product_id, name, category, description, price, merchant_id, image_url "
                + " FROM Products"
                + " Where status != 'DEACTIVATED'";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product p = new Product(
                        rs.getString("product_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("merchant_id"),
                        rs.getString("image_url"));
                // image
                // for UI
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int countByMerchantId(String merchantId) {
        String sql = "SELECT COUNT(*) FROM Products WHERE merchant_id = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, merchantId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Product findById(String productId) {
        String sql = "SELECT product_id, name, category, description, price, merchant_id FROM Products WHERE product_id = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, productId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getString("product_id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getString("description"),
                            rs.getDouble("price"),
                            rs.getString("merchant_id"),
                            "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&h=600&fit=crop"); // Default
                    // image
                    // for
                    // UI
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

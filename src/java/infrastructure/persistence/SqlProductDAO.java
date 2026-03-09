package infrastructure.persistence;

import application.ports.IProductRepository;
import domain.entities.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SqlProductDAO implements IProductRepository {

    private static final String DEFAULT_IMAGE = "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&h=600&fit=crop";

    private String tryGetImageUrl(ResultSet rs) {
        try {
            String url = rs.getString("image_url");
            return url != null ? url : DEFAULT_IMAGE;
        } catch (Exception e) {
            return DEFAULT_IMAGE;
        }
    }

    @Override
    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Products";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Product(
                        rs.getString("product_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("category"),
                        rs.getString("status"),
                        rs.getString("merchant_id"),
                        tryGetImageUrl(rs)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Product> findByMerchantId(String merchantId) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE merchant_id = ? ORDER BY name ASC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, merchantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Product(
                            rs.getString("product_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("price"),
                            rs.getString("category"),
                            rs.getString("status"),
                            rs.getString("merchant_id"),
                            tryGetImageUrl(rs)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Product> findByCategory(String category) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE category = ? AND status = 'ACTIVE' ORDER BY name ASC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Product(
                            rs.getString("product_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("price"),
                            rs.getString("category"),
                            rs.getString("status"),
                            rs.getString("merchant_id"),
                            tryGetImageUrl(rs)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int countByMerchantId(String merchantId) {
        String sql = "SELECT COUNT(*) FROM Products WHERE merchant_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, merchantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Product findById(String productId) {
        String sql = "SELECT * FROM Products WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getString("product_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("price"),
                            rs.getString("category"),
                            rs.getString("status"),
                            rs.getString("merchant_id"),
                            tryGetImageUrl(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean save(Product product) {
        String sql = "INSERT INTO Products (product_id, name, description, price, category, status, merchant_id, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getProductId());
            ps.setString(2, product.getName());
            ps.setString(3, product.getDescription());
            ps.setDouble(4, product.getPrice());
            ps.setString(5, product.getCategory());
            ps.setString(6, product.getStatus());
            ps.setString(7, product.getMerchantId());
            ps.setString(8, product.getImageUrl());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Product product) {
        boolean hasNewImage = product.getImageUrl() != null && !product.getImageUrl().isEmpty();
        String sql = hasNewImage
                ? "UPDATE Products SET name=?, description=?, category=?, image_url=?, status=? WHERE product_id=? AND merchant_id=?"
                : "UPDATE Products SET name=?, description=?, category=?, status=? WHERE product_id=? AND merchant_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            if (hasNewImage) {
                ps.setString(1, product.getName());
                ps.setString(2, product.getDescription());
                ps.setString(3, product.getCategory());
                ps.setString(4, product.getImageUrl());
                ps.setString(5, product.getStatus());
                ps.setString(6, product.getProductId());
                ps.setString(7, product.getMerchantId());
            } else {
                ps.setString(1, product.getName());
                ps.setString(2, product.getDescription());
                ps.setString(3, product.getCategory());
                ps.setString(4, product.getStatus());
                ps.setString(5, product.getProductId());
                ps.setString(6, product.getMerchantId());
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

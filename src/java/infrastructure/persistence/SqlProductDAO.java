package infrastructure.persistence;

import application.ports.IProductRepository;
import domain.entities.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SqlProductDAO implements IProductRepository {

    private String tryGetImageUrl(ResultSet rs) {
        try {
            return rs.getString("image_url");
        } catch (Exception e) {
            return null;
        }
    }

    private Product mapRow(ResultSet rs) throws Exception {
        return new Product(
                rs.getString("product_id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getString("description"),
                rs.getDouble("price"),
                rs.getString("merchant_id"),
                tryGetImageUrl(rs));
    }

    @Override
    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT product_id, name, category, description, price, merchant_id, image_url "
                + "FROM Products WHERE status != 'DEACTIVATED'";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Product> findByMerchantId(String merchantId) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT product_id, name, category, description, price, merchant_id, image_url, status "
                + "FROM Products WHERE merchant_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, merchantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = mapRow(rs);
                    p.setStatus(domain.enums.ProductStatus.valueOf(rs.getString("status")));
                    list.add(p);
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
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
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
        String sql = "SELECT product_id, name, category, description, price, merchant_id, image_url "
                + "FROM Products "
                + "WHERE product_id = ? "
                + "AND status != 'DEACTIVATED'";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return mapRow(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean save(Product product) {
        String sql = "INSERT INTO Products (product_id, name, category, description, price, merchant_id, image_url, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getProductId());
            ps.setString(2, product.getName());
            ps.setString(3, product.getCategory());
            ps.setString(4, product.getDescription());
            ps.setDouble(5, product.getPrice());
            ps.setString(6, product.getMerchantId());
            if (product.getImageUrl() != null) {
                ps.setString(7, product.getImageUrl());
            } else {
                ps.setNull(7, java.sql.Types.VARCHAR);
            }
            ps.setString(8, product.getStatus() != null ? product.getStatus().name() : "PENDING");
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("save() failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Product product) {
        boolean hasImage = product.getImageUrl() != null && !product.getImageUrl().isEmpty();
        String sql = hasImage
                ? "UPDATE Products SET name=?, category=?, description=?, price=?, image_url=?, status=? "
                + "WHERE product_id=? "
                + "AND merchant_id=?"
                : "UPDATE Products SET name=?, category=?, description=?, price=?, status=? "
                + "WHERE product_id=? "
                + "AND merchant_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getCategory());
            ps.setString(3, product.getDescription());
            ps.setDouble(4, product.getPrice());
            if (hasImage) {
                ps.setString(5, product.getImageUrl());
                ps.setString(6, product.getStatus() != null ? product.getStatus().name() : "ACTIVE");
                ps.setString(7, product.getProductId());
                ps.setString(8, product.getMerchantId());
            } else {
                ps.setString(5, product.getStatus() != null ? product.getStatus().name() : "ACTIVE");
                ps.setString(6, product.getProductId());
                ps.setString(7, product.getMerchantId());
            }
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("update() matched 0 rows. productId=" + product.getProductId()
                        + " merchantId=" + product.getMerchantId());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("update() failed: " + e.getMessage(), e);
        }
    }
    
    public List<Product> findByCategory(String category) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT product_id, name, category, description, price, merchant_id, image_url, status "
                + "FROM Products "
                + "WHERE category = ? "
                + "AND state != 'DEACTIVATED"
                + "ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = mapRow(rs);
                    p.setStatus(domain.enums.ProductStatus.valueOf(rs.getString("status")));
                    list.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}

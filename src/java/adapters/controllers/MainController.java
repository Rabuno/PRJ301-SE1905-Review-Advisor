package adapters.controllers;

import application.services.ProductService;
import domain.entities.Product;
import infrastructure.persistence.SqlProductDAO;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "MainController", urlPatterns = {"/MainController"})
public class MainController extends HttpServlet {

    private ProductService productService;

    @Override
    public void init() throws ServletException {
        // Cấu hình Dependency Injection nội bộ
        this.productService = new ProductService(new SqlProductDAO());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // 1. Lấy dữ liệu danh sách sản phẩm
            List<Product> products = productService.getAllProducts();
            
            // 2. Gắn vào Request Scope để truyền sang JSP
            request.setAttribute("PRODUCT_LIST", products);
            
            // 3. Chuyển tiếp (Forward) tới View giao diện trang chủ
            request.getRequestDispatcher("/views/customer/index.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("ERROR", "Lỗi tải trang chủ: " + e.getMessage());
            request.getRequestDispatcher("/views/shared/error.jsp").forward(request, response);
        }
    }
}
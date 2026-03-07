package adapters.controllers;

import application.services.ProductService;
import application.services.ReviewService;
import domain.entities.Product;
import domain.entities.Review;
import infrastructure.ai.WekaProvider;
import infrastructure.persistence.SqlProductDAO;
import infrastructure.persistence.SqlReviewDAO;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "MainController", urlPatterns = { "/MainController" })
public class MainController extends HttpServlet {

    private ProductService productService;
    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        // Cấu hình Dependency Injection nội bộ
        this.productService = new ProductService(new SqlProductDAO());
        this.reviewService = new ReviewService(new SqlReviewDAO(), new WekaProvider());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("ViewDetail".equals(action)) {
                String productId = request.getParameter("id");

                Product product = productService.getProductById(productId);
                List<Review> reviews = reviewService.getReviewsByProduct(productId);

                if (product != null) {
                    request.setAttribute("PRODUCT", product);
                    request.setAttribute("REVIEWS", reviews);
                    request.getRequestDispatcher("/views/customer/product-detail.jsp").forward(request, response);
                } else {
                    request.setAttribute("ERROR", "Product not found!");
                    request.getRequestDispatcher("/views/shared/error.jsp").forward(request, response);
                }
            } else {
                // Default action: Lấy dữ liệu danh sách sản phẩm
                List<Product> products = productService.getAllProducts();
                request.setAttribute("PRODUCT_LIST", products);
                request.getRequestDispatcher("/views/customer/index.jsp").forward(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("ERROR", "Lỗi tải trang: " + e.getMessage());
            request.getRequestDispatcher("/views/shared/error.jsp").forward(request, response);
        }
    }
}
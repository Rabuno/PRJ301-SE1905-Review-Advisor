package adapters.controllers;

import application.dto.ProductReviewStatsDTO;
import application.services.ProductService;
import application.services.ReviewService;
import domain.entities.Product;
import domain.entities.Review;
import domain.entities.User;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "MainController", urlPatterns = {"/MainController"})
public class MainController extends BaseServlet { // Kế thừa BaseServlet

    private ProductService productService;
    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        // Lấy các Singleton Service đã được AppConfigListener khởi tạo
        this.reviewService = (ReviewService) getServletContext().getAttribute("ReviewService");
        this.productService = (ProductService) getServletContext().getAttribute("ProductService");

        if (this.reviewService == null || this.productService == null) {
            throw new ServletException("Hệ thống chưa nạp được các Service phụ thuộc.");
        }
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
                    ProductReviewStatsDTO stats = new ProductReviewStatsDTO(reviews);

                    request.setAttribute("PRODUCT", product);
                    request.setAttribute("REVIEWS", reviews);
                    request.setAttribute("TOTAL_REVIEWS", stats.getTotalReviews());
                    request.setAttribute("AVERAGE_RATING", stats.getAverageRatingFormatted());
                    request.setAttribute("STAR_COUNTS", stats.getStarCounts());
                    forwardToView(request, response, "/views/customer/product-detail.jsp"); // Sử dụng hàm từ BaseServlet
                } else {
                    request.setAttribute("ERROR", "Product not found!");
                    forwardToView(request, response, "/views/shared/error.jsp"); // Sử dụng hàm từ BaseServlet
                }
            } else if ("MyReviews".equals(action)) {
                HttpSession session = request.getSession();
                User user = (User) session.getAttribute("USER");
                if (user != null) {
                    List<Review> myReviews = reviewService.getReviewsByUser(user.getUserId());
                    request.setAttribute("MY_REVIEWS", myReviews);
                    forwardToView(request, response, "/views/customer/my-reviews.jsp"); // Sử dụng hàm từ BaseServlet
                } else {
                    redirect(request, response, "/login.jsp"); // Sử dụng hàm từ BaseServlet
                }
            } else if ("FilterByCategory".equals(action)) {
                String category = request.getParameter("category");
                List<Product> products;
                if (category != null && !category.trim().isEmpty()) {
                    products = productService.findByCategory(category.trim());
                    request.setAttribute("ACTIVE_CATEGORY", category.trim());
                } else {
                    products = productService.getAllProducts();
                }
                request.setAttribute("PRODUCT_LIST", products);
                forwardToView(request, response, "/views/customer/index.jsp"); // Sử dụng hàm từ BaseServlet
            } else if ("search".equals(action)) {
                String keyword = request.getParameter("txtSearch");
                List<Product> searchResults = productService.searchProducts(keyword);
                request.setAttribute("PRODUCT_LIST", searchResults);
                forwardToView(request, response, "/views/customer/index.jsp");
            } else {
                List<Product> products = productService.getAllProducts();
                request.setAttribute("PRODUCT_LIST", products);
                forwardToView(request, response, "/views/customer/index.jsp"); // Sử dụng hàm từ BaseServlet
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("ERROR", "Lỗi tải trang: " + e.getMessage());
            forwardToView(request, response, "/views/shared/error.jsp"); // Sử dụng hàm từ BaseServlet
        }
    }
}

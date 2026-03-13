package adapters.controllers;

import application.dto.ProductReviewStatsDTO;
import application.services.ProductService;
import application.services.RecommendationService;
import application.services.ReviewService;
import domain.entities.Product;
import domain.entities.Review;
import domain.entities.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "MainController", urlPatterns = {"/MainController"})
public class MainController extends BaseServlet { // Kế thừa BaseServlet

    private ProductService productService;
    private ReviewService reviewService;
    private RecommendationService recommendationService;

    @Override
    public void init() throws ServletException {
        // Lấy các Singleton Service đã được AppConfigListener khởi tạo
        this.reviewService = (ReviewService) getServletContext().getAttribute("ReviewService");
        this.productService = (ProductService) getServletContext().getAttribute("ProductService");
        this.recommendationService = (RecommendationService) getServletContext().getAttribute("RecommendationService");

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
                    request.setAttribute("AVERAGE_RATING_RAW", stats.getAverageRating());
                    request.setAttribute("STAR_COUNTS", stats.getStarCounts());

                    // Related products (same category), excluding current
                    List<Product> related = productService.findByCategory(product.getCategory());
                    if (related != null) {
                        related.removeIf(p -> p == null || productId.equals(p.getProductId()));
                    }
                    if (recommendationService != null && related != null && !related.isEmpty()) {
                        HttpSession session = request.getSession(false);
                        User u = (session == null) ? null : (User) session.getAttribute("USER");
                        related = recommendationService.recommendForUser(u, related, 3);
                    } else if (related != null && related.size() > 3) {
                        related = related.subList(0, 3);
                    }
                    request.setAttribute("RELATED_PRODUCTS", related);
                    addProductRatingsToRequest(request, related);
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
                addProductRatingsToRequest(request, products);
                addRecommendationsToRequest(request, products);
                forwardToView(request, response, "/views/customer/index.jsp"); // Sử dụng hàm từ BaseServlet
            } else if ("search".equals(action)) {
                String keyword = request.getParameter("txtSearch");
                List<Product> searchResults = productService.searchProducts(keyword);
                request.setAttribute("PRODUCT_LIST", searchResults);
                addProductRatingsToRequest(request, searchResults);
                addRecommendationsToRequest(request, searchResults);
                forwardToView(request, response, "/views/customer/index.jsp");
            } else {
                List<Product> products = productService.getAllProducts();
                request.setAttribute("PRODUCT_LIST", products);
                addProductRatingsToRequest(request, products);
                addRecommendationsToRequest(request, products);
                forwardToView(request, response, "/views/customer/index.jsp"); // Sử dụng hàm từ BaseServlet
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("ERROR", "Lỗi tải trang: " + e.getMessage());
            forwardToView(request, response, "/views/shared/error.jsp"); // Sử dụng hàm từ BaseServlet
        }
    }

    private void addProductRatingsToRequest(HttpServletRequest request, List<Product> products) {
        // Map productId -> average rating (0.0 if no reviews)
        Map<String, Double> avgRatings = new HashMap<>();
        Map<String, Integer> reviewCounts = new HashMap<>();
        if (products != null) {
            for (Product p : products) {
                if (p == null || p.getProductId() == null) {
                    continue;
                }
                List<Review> reviews = reviewService.getReviewsByProduct(p.getProductId());
                ProductReviewStatsDTO stats = new ProductReviewStatsDTO(reviews);
                avgRatings.put(p.getProductId(), stats.getAverageRating());
                reviewCounts.put(p.getProductId(), stats.getTotalReviews());
            }
        }
        request.setAttribute("PRODUCT_AVG_RATINGS", avgRatings);
        request.setAttribute("PRODUCT_REVIEW_COUNTS", reviewCounts);
    }

    private void addRecommendationsToRequest(HttpServletRequest request, List<Product> products) {
        if (recommendationService == null) return;
        HttpSession session = request.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("USER");

        // Keep it small for UI; index.jsp can choose to render or ignore this attribute.
        List<Product> rec = recommendationService.recommendForUser(user, products, 3);
        request.setAttribute("RECOMMENDED_PRODUCTS", rec);
    }
}

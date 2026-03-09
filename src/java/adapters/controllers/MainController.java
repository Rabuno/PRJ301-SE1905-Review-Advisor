package adapters.controllers;

import application.ports.IAlertRepository;
import application.ports.IReviewRepository;
import application.ports.IUserRepository;
import application.services.ProductService;
import application.services.ReviewService;
import application.services.TriageService;
import domain.entities.Product;
import domain.entities.Review;
import domain.entities.User;
import infrastructure.ai.WekaProvider;
import infrastructure.persistence.SqlAlertDAO;
import infrastructure.persistence.SqlProductDAO;
import infrastructure.persistence.SqlReviewDAO;
import infrastructure.persistence.SqlUserDAO;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "MainController", urlPatterns = {"/MainController"})
public class MainController extends HttpServlet {

    private ProductService productService;
    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        try {
            application.ports.IReviewRepository reviewDAO = new infrastructure.persistence.SqlReviewDAO();
            application.ports.IUserRepository userDAO = new infrastructure.persistence.SqlUserDAO();
            application.ports.IAlertRepository alertDAO = new infrastructure.persistence.SqlAlertDAO();

            // ĐIỂM CẬP NHẬT: Khởi tạo IFileStoragePort để khớp với ReviewService mới
            String uploadDirPath = getServletContext().getRealPath("/assets/uploads");
            application.ports.IFileStoragePort storagePort = new infrastructure.storage.LocalFileStorageAdapter(uploadDirPath);

            // Định vị tệp Model Weka
            String modelPath = getServletContext().getRealPath("/WEB-INF/model/spam_review_classifier.model");
            application.services.TriageService triageService = new application.services.TriageService(new infrastructure.ai.WekaProvider(modelPath));

            // Bổ sung tham số storagePort vào Constructor
            this.reviewService = new application.services.ReviewService(reviewDAO, userDAO, alertDAO, triageService, storagePort);

        } catch (Exception e) {
            throw new javax.servlet.ServletException("Lỗi nạp Model tại ModeratorServlet: " + e.getMessage());
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
                    request.setAttribute("PRODUCT", product);
                    request.setAttribute("REVIEWS", reviews);
                    request.getRequestDispatcher("/views/customer/product-detail.jsp").forward(request, response);
                } else {
                    request.setAttribute("ERROR", "Product not found!");
                    request.getRequestDispatcher("/views/shared/error.jsp").forward(request, response);
                }
            } else if ("MyReviews".equals(action)) {
                HttpSession session = request.getSession();
                User user = (User) session.getAttribute("USER");
                if (user != null) {
                    List<Review> myReviews = reviewService.getReviewsByUser(user.getUserId());
                    request.setAttribute("MY_REVIEWS", myReviews);
                    request.getRequestDispatcher("/views/customer/my-reviews.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/login.jsp");
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

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

@WebServlet(name = "MainController", urlPatterns = { "/MainController" })
public class MainController extends HttpServlet {

    private ProductService productService;
    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        try {
            application.ports.IProductRepository productDAO = new SqlProductDAO();
            this.productService = new ProductService(productDAO);

            IReviewRepository reviewDAO = new SqlReviewDAO();
            IUserRepository userDAO = new SqlUserDAO();

            // 1. KỸ THUẬT MOCKING: Giả lập AlertDAO tạm thời để code không báo đỏ chờ DB
            IAlertRepository alertDAO = new SqlAlertDAO(); // Khởi tạo kết nối SQL Server thật

            // 2. Định vị tệp Model Weka tự động trên Tomcat (Đã bỏ chữ /classes/)
            String modelPath = getServletContext().getRealPath("/WEB-INF/model/spam_review_classifier.model");
            TriageService triageService = new TriageService(new WekaProvider(modelPath));

            // 3. Khởi tạo ReviewService chính thức (Gán vào biến instance)
            this.reviewService = new ReviewService(reviewDAO, userDAO, alertDAO, triageService);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Lỗi khởi tạo ReviewServlet do Weka Model: " + e.getMessage());
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
            } else if ("FilterByCategory".equals(action)) {
                // Loc san pham theo danh muc
                // UI: goi ?action=FilterByCategory&category=Hotel
                String category = request.getParameter("category");
                List<Product> products;
                if (category != null && !category.trim().isEmpty()) {
                    products = productService.getProductsByCategory(category.trim());
                    request.setAttribute("ACTIVE_CATEGORY", category.trim()); // UI dung de highlight tab dang chon
                } else {
                    products = productService.getAllProducts();
                }
                request.setAttribute("PRODUCT_LIST", products);
                request.getRequestDispatcher("/views/customer/index.jsp").forward(request, response);
            } else {
                // Default: Lay danh sach tat ca san pham
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
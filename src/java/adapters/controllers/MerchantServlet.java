package adapters.controllers;

import application.dto.MerchantStatsDTO;
import application.ports.IAlertRepository;
import application.ports.IReviewRepository;
import application.ports.IUserRepository;
import application.services.ProductService;
import application.services.ReviewService;
import application.services.TriageService;
import domain.entities.Review;
import domain.entities.User;
import infrastructure.ai.WekaProvider;
import infrastructure.persistence.SqlAlertDAO;
import infrastructure.persistence.SqlProductDAO;
import application.ports.IProductRepository;
import infrastructure.persistence.SqlReviewDAO;
import infrastructure.persistence.SqlUserDAO;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "MerchantServlet", urlPatterns = { "/MerchantServlet" })
public class MerchantServlet extends HttpServlet {

    private ProductService productService;
    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        try {
            IReviewRepository reviewDAO = new SqlReviewDAO();
            IUserRepository userDAO = new SqlUserDAO();

            // 1. KỸ THUẬT MOCKING: Giả lập AlertDAO tạm thời để code không báo đỏ chờ DB
            IAlertRepository alertDAO = new SqlAlertDAO(); // Khởi tạo kết nối SQL Server thật

            // Khởi tạo Product DAO và Service
            IProductRepository productDAO = new SqlProductDAO();
            this.productService = new ProductService(productDAO);

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

        User user = (User) request.getSession().getAttribute("USER");

        if (user == null || !"MERCHANT".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/views/shared/accessDenied.jsp");
            return;
        }

        try {
            String merchantId = user.getUserId();

            // Lấy tổng số sản phẩm của Merchant
            int totalProperties = productService.countPropertiesByMerchant(merchantId);

            // Lấy các chỉ số thống kê ([0]: avgRating, [1]: publishedCount, [2]:
            // flaggedCount)
            Object[] statsArray = reviewService.getMerchantReviewStats(merchantId);
            double avgRating = (double) statsArray[0];
            int publishedCount = (int) statsArray[1];
            int flaggedCount = (int) statsArray[2];

            MerchantStatsDTO statsDTO = new MerchantStatsDTO(totalProperties, avgRating, publishedCount, flaggedCount);

            // Lấy 5 đánh giá mới nhất
            List<Review> recentReviews = reviewService.getRecentMerchantReviews(merchantId, 5);

            request.setAttribute("STATS", statsDTO);
            request.setAttribute("RECENT_FEEDBACK", recentReviews);

            request.getRequestDispatcher("/views/merchant/merchant-dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("ERROR", "Lỗi tải dữ liệu Dashboard: " + e.getMessage());
            request.getRequestDispatcher("/views/shared/error.jsp").forward(request, response);
        }
    }
}

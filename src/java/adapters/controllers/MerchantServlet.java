package adapters.controllers;

import application.dto.MerchantStatsDTO;
import application.ports.IAlertRepository;
import application.ports.IFileStoragePort;
import application.ports.IProductRepository;
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
import infrastructure.persistence.SqlReviewDAO;
import infrastructure.persistence.SqlUserDAO;
import infrastructure.storage.LocalFileStorageAdapter;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "MerchantServlet", urlPatterns = {"/MerchantServlet"})
public class MerchantServlet extends HttpServlet {

    private ProductService productService;
    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        try {
            IReviewRepository reviewDAO = new SqlReviewDAO();
            IUserRepository userDAO = new SqlUserDAO();
            IAlertRepository alertDAO = new SqlAlertDAO();
            IProductRepository productDAO = new SqlProductDAO();

            // ĐIỂM CẬP NHẬT: Khởi tạo IFileStoragePort để khớp với ReviewService mới
            String uploadDirPath = getServletContext().getRealPath("/assets/uploads");
            IFileStoragePort storagePort = new LocalFileStorageAdapter(uploadDirPath);

            // Định vị tệp Model Weka
            String modelPath = getServletContext().getRealPath("/WEB-INF/model/spam_review_classifier.model");
            TriageService triageService = new TriageService(new WekaProvider(modelPath));

            // Bổ sung tham số storagePort vào Constructor
            this.reviewService = new ReviewService(reviewDAO, userDAO, alertDAO, triageService, storagePort);
            this.productService = new ProductService(productDAO);

        } catch (Exception e) {
            throw new javax.servlet.ServletException("Lỗi nạp Model tại ModeratorServlet: " + e.getMessage());
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

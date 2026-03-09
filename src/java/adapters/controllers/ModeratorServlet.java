package adapters.controllers;

import application.ports.IAlertRepository;
import application.services.ReviewService;
import domain.entities.Review;
import domain.entities.User;
import domain.enums.Status;
import infrastructure.persistence.SqlAlertDAO;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ModeratorServlet", urlPatterns = {"/ModeratorServlet"})
public class ModeratorServlet extends HttpServlet {

    private ReviewService reviewService;

    // Tại hàm init() của ModeratorServlet.java
    @Override
    public void init() throws javax.servlet.ServletException {
        try {
            application.ports.IReviewRepository reviewDAO = new infrastructure.persistence.SqlReviewDAO();
            application.ports.IUserRepository userDAO = new infrastructure.persistence.SqlUserDAO();

            // Dummy AlertDAO cho đến khi có DB thật
            IAlertRepository alertDAO = new SqlAlertDAO(); // Khởi tạo kết nối SQL Server thật

            // 2. Định vị tệp Model Weka tự động trên Tomcat (Đã bỏ chữ /classes/)
            String modelPath = getServletContext().getRealPath("/WEB-INF/model/spam_review_classifier.model");
            application.services.TriageService triageService = new application.services.TriageService(new infrastructure.ai.WekaProvider(modelPath));

            // Khởi tạo thành công
            this.reviewService = new application.services.ReviewService(reviewDAO, userDAO, alertDAO, triageService);

        } catch (Exception e) {
            throw new javax.servlet.ServletException("Lỗi nạp Model tại ModeratorServlet: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy danh sách các đánh giá đang bị giam (FLAGGED)
            List<Review> flaggedReviews = reviewService.getFlaggedReviews();
            request.setAttribute("FLAGGED_REVIEWS", flaggedReviews);

            // Forward tới giao diện Dashboard
            request.getRequestDispatcher("/views/moderation/dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("ERROR", "Lỗi tải dữ liệu kiểm duyệt: " + e.getMessage());
            request.getRequestDispatcher("/views/shared/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("USER");

        if (user == null || !user.hasPermission("PERM_MODERATE_ACTION")) {
            response.sendRedirect(request.getContextPath() + "/views/shared/accessDenied.jsp");
            return;
        }

        String reviewId = request.getParameter("reviewId");
        String action = request.getParameter("action");

        try {
            if ("APPROVE".equalsIgnoreCase(action)) {
                reviewService.moderateReview(reviewId, Status.PUBLISHED);
                request.getSession().setAttribute("SUCCESS_MSG", "Đã phê duyệt đánh giá " + reviewId);
            } else if ("REJECT".equalsIgnoreCase(action)) {
                reviewService.moderateReview(reviewId, Status.HIDDEN);
                request.getSession().setAttribute("SUCCESS_MSG", "Đã gỡ bỏ đánh giá " + reviewId);
            }

            response.sendRedirect(request.getContextPath() + "/ModeratorServlet");
        } catch (Exception e) {
            request.setAttribute("ERROR", "Lỗi xử lý kiểm duyệt: " + e.getMessage());
            request.getRequestDispatcher("/views/shared/error.jsp").forward(request, response);
        }
    }
}

package adapters.controllers;

import application.services.ReviewService;
import domain.entities.Review;
import domain.entities.User;
import domain.enums.ReviewStatus;

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
        try {
            // CẬP NHẬT: Sử dụng DTO được làm giàu dữ liệu thay vì Entity thô
            List<application.dto.AlertDashboardDTO> flaggedReviews = reviewService.getFlaggedReviewsForDashboard();

            // Truyền DTO sang giao diện
            request.setAttribute("FLAGGED_REVIEWS", flaggedReviews);

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
                reviewService.moderateReview(reviewId, ReviewStatus.PUBLISHED);
                request.getSession().setAttribute("SUCCESS_MSG", "Đã phê duyệt đánh giá " + reviewId);
            } else if ("REJECT".equalsIgnoreCase(action)) {
                reviewService.moderateReview(reviewId, ReviewStatus.HIDDEN);
                request.getSession().setAttribute("SUCCESS_MSG", "Đã gỡ bỏ đánh giá " + reviewId);
            }

            response.sendRedirect(request.getContextPath() + "/ModeratorServlet");
        } catch (Exception e) {
            request.setAttribute("ERROR", "Lỗi xử lý kiểm duyệt: " + e.getMessage());
            request.getRequestDispatcher("/views/shared/error.jsp").forward(request, response);
        }
    }
}

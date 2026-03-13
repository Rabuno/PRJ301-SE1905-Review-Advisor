package adapters.controllers;

import application.services.AuditService;
import application.services.ReviewService;
import application.dto.AlertDashboardDTO;
import domain.entities.User;
import domain.enums.ReviewStatus;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ModeratorServlet", urlPatterns = {"/ModeratorServlet"})
public class ModeratorServlet extends BaseServlet { // Kế thừa BaseServlet

    private ReviewService reviewService;
    private AuditService auditService;

    @Override
    public void init() throws ServletException {
        // Lấy các Singleton Service đã được AppConfigListener khởi tạo
        this.reviewService = (ReviewService) getServletContext().getAttribute("ReviewService");
        this.auditService = (AuditService) getServletContext().getAttribute("AuditService");
        
        if (this.reviewService == null || this.auditService == null) {
            throw new ServletException("Hệ thống chưa nạp được các Service phụ thuộc.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<AlertDashboardDTO> flaggedReviews = reviewService.getFlaggedReviewsForDashboard();
            request.setAttribute("FLAGGED_REVIEWS", flaggedReviews);
            forwardToView(request, response, "/views/moderation/dashboard.jsp"); // Sử dụng BaseServlet
        } catch (Exception e) {
            request.setAttribute("ERROR", "Lỗi tải dữ liệu kiểm duyệt: " + e.getMessage());
            forwardToView(request, response, "/views/shared/error.jsp"); // Sử dụng BaseServlet
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("USER");

        if (user == null || !user.hasPermission("PERM_MODERATE_ACTION")) {
            redirect(request, response, "/views/shared/accessDenied.jsp"); // Sử dụng BaseServlet
            return;
        }

        String reviewId = request.getParameter("reviewId");
        String action = request.getParameter("action");

        try {
            if ("APPROVE".equalsIgnoreCase(action)) {
                reviewService.moderateReview(reviewId, ReviewStatus.PUBLISHED);
                auditService.logAction(user.getUserId(), "APPROVE_REVIEW", "{\"reviewId\":\"" + reviewId + "\"}");
                request.getSession().setAttribute("SUCCESS_MSG", "Đã phê duyệt đánh giá " + reviewId);
            } else if ("REJECT".equalsIgnoreCase(action)) {
                reviewService.moderateReview(reviewId, ReviewStatus.HIDDEN);
                auditService.logAction(user.getUserId(), "REJECT_REVIEW", "{\"reviewId\":\"" + reviewId + "\"}");
                request.getSession().setAttribute("SUCCESS_MSG", "Đã gỡ bỏ đánh giá " + reviewId);
            }

            redirect(request, response, "/ModeratorServlet"); // Sử dụng BaseServlet
        } catch (Exception e) {
            request.setAttribute("ERROR", "Lỗi xử lý kiểm duyệt: " + e.getMessage());
            forwardToView(request, response, "/views/shared/error.jsp"); // Sử dụng BaseServlet
        }
    }
}
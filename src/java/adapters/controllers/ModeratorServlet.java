package adapters.controllers;

import application.services.ReviewService;
import domain.entities.Review;
import domain.entities.User;
import domain.enums.ReviewStatus;
import infrastructure.ai.WekaProvider;
import infrastructure.persistence.SqlReviewDAO;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ModeratorServlet", urlPatterns = { "/ModeratorServlet" })
public class ModeratorServlet extends HttpServlet {

    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        SqlReviewDAO reviewDAO = new SqlReviewDAO();
        WekaProvider aiProvider = new WekaProvider();
        this.reviewService = new ReviewService(reviewDAO, aiProvider);
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
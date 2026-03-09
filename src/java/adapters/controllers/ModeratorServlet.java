package adapters.controllers;

import adapters.dto.FlaggedReviewDTO;
import application.ports.IAlertRepository;
import application.services.ReviewService;
import domain.entities.Alert;
import domain.entities.Review;
import domain.entities.User;
import domain.enums.Status;
import infrastructure.persistence.SqlAlertDAO;

import java.io.IOException;
import java.util.ArrayList;
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
    public void init() throws javax.servlet.ServletException {
        super.init();
        try {
            application.ports.IReviewRepository reviewDAO = new infrastructure.persistence.SqlReviewDAO();
            application.ports.IUserRepository userDAO = new infrastructure.persistence.SqlUserDAO();
            IAlertRepository alertDAO = new SqlAlertDAO();

            // Thử nạp Weka model, nếu thất bại thì dùng null (chỉ mất chức năng AI, app vẫn
            // chạy)
            application.services.TriageService triageService = null;
            try {
                String modelPath = getServletContext().getRealPath("/WEB-INF/model/spam_review_classifier.model");
                triageService = new application.services.TriageService(new infrastructure.ai.WekaProvider(modelPath));
            } catch (Exception wekaEx) {
                System.err.println("[WARN] ModeratorServlet: Không thể tải Weka model: " + wekaEx.getMessage());
            }

            this.reviewService = new application.services.ReviewService(reviewDAO, userDAO, alertDAO, triageService);

        } catch (Exception e) {
            throw new javax.servlet.ServletException("Lỗi khởi tạo ModeratorServlet: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lay danh sach cac review FLAGGED
            List<Review> flaggedReviews = reviewService.getFlaggedReviews();

            // Gop Review + Alert thanh DTO de truyen sang JSP
            IAlertRepository alertDAO = new SqlAlertDAO();
            List<FlaggedReviewDTO> flaggedItems = new ArrayList<>();
            for (Review r : flaggedReviews) {
                Alert alert = alertDAO.findByReviewId(r.getReviewId());
                flaggedItems.add(new FlaggedReviewDTO(r, alert));
            }

            request.setAttribute("FLAGGED_ITEMS", flaggedItems);
            request.getRequestDispatcher("/views/moderation/dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("ERROR_MESSAGE", "Loi tai du lieu kiem duyet: " + e.getMessage());
            request.getRequestDispatcher("/views/shared/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("USER");

        if (user == null || (!user.hasPermission("PERM_MODERATE_ACTION") && !"ADMIN".equals(user.getRole()))) {
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
            request.setAttribute("ERROR_MESSAGE", "Lỗi xử lý kiểm duyệt: " + e.getMessage());
            request.getRequestDispatcher("/views/shared/error.jsp").forward(request, response);
        }
    }
}

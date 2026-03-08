package adapters.controllers;

import application.ports.IAlertRepository;
import application.ports.IReviewRepository;
import application.ports.IUserRepository;
import application.services.ReviewService;
import application.services.TriageService;
import domain.entities.Alert;
import domain.entities.Review;
import domain.entities.User;
import infrastructure.ai.WekaProvider;
import infrastructure.persistence.SqlAlertDAO;
import infrastructure.persistence.SqlReviewDAO;
import infrastructure.persistence.SqlUserDAO;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javax.servlet.annotation.MultipartConfig;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
@WebServlet(name = "ReviewServlet", urlPatterns = { "/ReviewServlet" })
public class ReviewServlet extends BaseServlet {

    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        try {
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
        if ("write".equals(action)) {
            forwardToView(request, response, "/views/customer/write-review.jsp");
        } else if ("edit".equals(action)) {
            String reviewId = request.getParameter("reviewId");
            Review existingReview = reviewService.getReviewById(reviewId);
            if (existingReview != null) {
                request.setAttribute("REVIEW", existingReview);
                forwardToView(request, response, "/views/customer/write-review.jsp");
            } else {
                request.setAttribute("ERROR", "Review not found!");
                redirect(request, response, "/MainController?action=MyReviews");
            }
        } else if ("delete".equals(action)) {
            try {
                String reviewId = request.getParameter("reviewId");
                String productId = request.getParameter("productId");
                String source = request.getParameter("source"); // To know where we came from

                reviewService.deleteReview(reviewId);
                request.getSession().setAttribute("SUCCESS_MSG", "Đã xóa đánh giá thành công!");

                if ("myreviews".equals(source)) {
                    redirect(request, response, "/MainController?action=MyReviews");
                } else if (productId != null && !productId.isEmpty()) {
                    redirect(request, response, "/MainController?action=ViewDetail&id=" + productId);
                } else {
                    redirect(request, response, "/MainController");
                }
            } catch (Exception e) {
                request.getSession().setAttribute("ERROR", "Lỗi khi xóa đánh giá: " + e.getMessage());
                redirect(request, response, "/MainController?action=MyReviews");
            }
        } else {
            forwardToView(request, response, "/views/customer/product-detail.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = request.getSession(false);
            User currentUser = (User) session.getAttribute("USER");

            String productId = request.getParameter("productId");
            String content = request.getParameter("content");
            if (content == null) {
                content = request.getParameter("txtContent");
            }

            int rating = Integer.parseInt(request.getParameter("rating"));

            String action = request.getParameter("action");
            if ("update".equals(action)) {
                String existingReviewId = request.getParameter("reviewId");
                Review updatedReview = new Review(existingReviewId, productId, currentUser.getUserId(), content,
                        rating);
                reviewService.submitReview(updatedReview, currentUser.getUsername()); // Re-runs AI triage

                request.getSession().setAttribute("SUCCESS_MSG", "Đánh giá của bạn đã được cập nhật thành công!");
                redirect(request, response, "/MainController?action=MyReviews");
            } else {
                String reviewId = "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                Review newReview = new Review(reviewId, productId, currentUser.getUserId(), content, rating);
                reviewService.submitReview(newReview, currentUser.getUsername());

                request.getSession().setAttribute("SUCCESS_MSG",
                        "Cảm ơn bạn đã gửi đánh giá! Hệ thống AI đang phân tích nội dung.");
                redirect(request, response, "/MainController?action=ViewDetail&id=" + productId);
            }

        } catch (Exception e) {
            request.setAttribute("ERROR", "Lỗi gửi đánh giá: " + e.getMessage());
            forwardToView(request, response, "/views/customer/write-review.jsp");
        }
    }
}

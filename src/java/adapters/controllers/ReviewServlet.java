package adapters.controllers;

import application.services.ReviewService;
import domain.entities.Review;
import domain.entities.User;
import infrastructure.ai.WekaProvider;
import infrastructure.persistence.SqlReviewDAO;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ReviewServlet", urlPatterns = { "/ReviewServlet" })
public class ReviewServlet extends BaseServlet {

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
        String action = request.getParameter("action");
        if ("write".equals(action)) {
            forwardToView(request, response, "/views/customer/write-review.jsp");
        } else {
            forwardToView(request, response, "/views/customer/product-detail.jsp");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = request.getSession(false);
            User currentUser = (User) session.getAttribute("USER");

            String productId = request.getParameter("productId");
            String content = request.getParameter("content");
            if (content == null)
                content = request.getParameter("txtContent");

            int rating = Integer.parseInt(request.getParameter("rating"));

            String reviewId = "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            Review newReview = new Review(reviewId, productId, currentUser.getUserId(), content, rating);

            reviewService.submitReview(newReview);

            request.getSession().setAttribute("SUCCESS_MSG", "Cảm ơn bạn đã gửi đánh giá! Đánh giá sẽ được ghi nhận.");
            redirect(request, response, "/MainController?action=ViewDetail&id=" + productId);

        } catch (Exception e) {
            request.setAttribute("ERROR", "Lỗi gửi đánh giá: " + e.getMessage());
            forwardToView(request, response, "/views/customer/write-review.jsp");
        }
    }
}

package adapters.controllers;

import application.services.ReviewService;
import domain.entities.Review;
import domain.entities.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.servlet.annotation.MultipartConfig;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
@WebServlet(name = "ReviewServlet", urlPatterns = {"/ReviewServlet"})
public class ReviewServlet extends BaseServlet {

    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        this.reviewService = (ReviewService) getServletContext().getAttribute("ReviewService");

        if (this.reviewService == null) {
            throw new ServletException("Hệ thống chưa nạp được các Service phụ thuộc.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // ... (Nội dung của doGet được giữ nguyên vẹn) ...
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
                String source = request.getParameter("source");

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

            // Xử lý lỗ hổng Parsing an toàn
            int rating = 0;
            try {
                rating = Integer.parseInt(request.getParameter("rating"));
                if (rating < 1 || rating > 5) {
                    throw new NumberFormatException("Rating out of bounds");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("ERROR", "Dữ liệu xếp hạng không hợp lệ. Vui lòng chọn từ 1 đến 5 sao.");
                forwardToView(request, response, "/views/customer/write-review.jsp");
                return; // Ngắt luồng thực thi ngay lập tức
            }

            // ĐIỂM CẬP NHẬT 2: Trích xuất giao diện nhị phân Part (Binary Extraction)
            Part filePart = request.getPart("evidenceImage");
            InputStream imageStream = null;
            String extension = "";

            if (filePart != null && filePart.getSize() > 0) {
                imageStream = filePart.getInputStream();
                String fileName = filePart.getSubmittedFileName();
                if (fileName != null && fileName.lastIndexOf(".") != -1) {
                    extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
                }
            }

            String action = request.getParameter("action");
            if ("update".equals(action)) {
                String existingReviewId = request.getParameter("reviewId");
                Review updatedReview = new Review(existingReviewId, productId, currentUser.getUserId(), content, rating);

                // ĐIỂM CẬP NHẬT 3: Ủy quyền toàn bộ luồng xử lý cho ReviewService
                reviewService.submitReview(updatedReview, currentUser, imageStream, extension);

                request.getSession().setAttribute("SUCCESS_MSG", "Đánh giá của bạn đã được cập nhật thành công!");
                redirect(request, response, "/MainController?action=MyReviews");
            } else {
                String reviewId = "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                Review newReview = new Review(reviewId, productId, currentUser.getUserId(), content, rating);

                // ĐIỂM CẬP NHẬT 3: Ủy quyền toàn bộ luồng xử lý cho ReviewService
                reviewService.submitReview(newReview, currentUser, imageStream, extension);

                request.getSession().setAttribute("SUCCESS_MSG", "Cảm ơn bạn đã gửi đánh giá! Hệ thống AI đang phân tích nội dung.");
                redirect(request, response, "/MainController?action=ViewDetail&id=" + productId);
            }

        } catch (Exception e) {
            request.setAttribute("ERROR", "Lỗi gửi đánh giá: " + e.getMessage());
            forwardToView(request, response, "/views/customer/write-review.jsp");
        }
    }
}

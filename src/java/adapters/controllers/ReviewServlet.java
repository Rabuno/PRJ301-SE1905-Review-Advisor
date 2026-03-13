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
    private application.services.AuditService auditService;

    @Override
    public void init() throws ServletException {
        this.reviewService = (ReviewService) getServletContext().getAttribute("ReviewService");
        this.auditService = (application.services.AuditService) getServletContext().getAttribute("AuditService");

        if (this.reviewService == null || this.auditService == null) {
            throw new ServletException("Hệ thống chưa nạp được các Service phụ thuộc.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // ... (Nội dung của doGet được giữ nguyên vẹn) ...
        String action = request.getParameter("action");
        if ("write".equals(action)) {
            HttpSession session = request.getSession(false);
            User u = (session == null) ? null : (User) session.getAttribute("USER");
            if (u == null || !u.hasPermission("PERM_REVIEW_CREATE")) {
                redirect(request, response, "/views/shared/accessDenied.jsp");
                return;
            }
            forwardToView(request, response, "/views/customer/write-review.jsp");
        } else if ("edit".equals(action)) {
            HttpSession session = request.getSession(false);
            User u = (session == null) ? null : (User) session.getAttribute("USER");
            if (u == null || !u.hasPermission("PERM_REVIEW_UPDATE")) {
                redirect(request, response, "/views/shared/accessDenied.jsp");
                return;
            }
            String reviewId = request.getParameter("reviewId");
            Review existingReview = reviewService.getReviewById(reviewId);
            if (existingReview != null) {
                if (!"ADMIN".equals(u.getRole()) && !existingReview.getUserId().equals(u.getUserId())) {
                    redirect(request, response, "/views/shared/accessDenied.jsp");
                    return;
                }
                request.setAttribute("REVIEW", existingReview);
                forwardToView(request, response, "/views/customer/write-review.jsp");
            } else {
                request.setAttribute("ERROR", "Review not found!");
                redirect(request, response, "/MainController?action=MyReviews");
            }
        } else if ("delete".equals(action)) {
            try {
                HttpSession session = request.getSession(false);
                User u = (session == null) ? null : (User) session.getAttribute("USER");
                if (u == null || !u.hasPermission("PERM_REVIEW_DELETE")) {
                    redirect(request, response, "/views/shared/accessDenied.jsp");
                    return;
                }
                String reviewId = request.getParameter("reviewId");
                String productId = request.getParameter("productId");
                String source = request.getParameter("source");

                Review existingReview = reviewService.getReviewById(reviewId);
                if (existingReview == null) {
                    redirect(request, response, "/MainController?action=MyReviews");
                    return;
                }
                if (!"ADMIN".equals(u.getRole()) && !existingReview.getUserId().equals(u.getUserId())) {
                    redirect(request, response, "/views/shared/accessDenied.jsp");
                    return;
                }

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
            if (session == null || session.getAttribute("USER") == null) {
                redirect(request, response, "/login.jsp");
                return;
            }
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
                if (!currentUser.hasPermission("PERM_REVIEW_UPDATE")) {
                    redirect(request, response, "/views/shared/accessDenied.jsp");
                    return;
                }
                String existingReviewId = request.getParameter("reviewId");
                Review existing = reviewService.getReviewById(existingReviewId);
                if (existing == null) {
                    request.setAttribute("ERROR", "Review not found!");
                    forwardToView(request, response, "/views/customer/write-review.jsp");
                    return;
                }
                if (!"ADMIN".equals(currentUser.getRole()) && !existing.getUserId().equals(currentUser.getUserId())) {
                    redirect(request, response, "/views/shared/accessDenied.jsp");
                    return;
                }
                Review updatedReview = new Review(existingReviewId, productId, currentUser.getUserId(), content, rating);

                // ĐIỂM CẬP NHẬT 3: Ủy quyền toàn bộ luồng xử lý cho ReviewService
                reviewService.submitReview(updatedReview, currentUser, imageStream, extension);

                auditService.logAction(currentUser.getUserId(), "REVIEW_SUBMITTED_UPDATE",
                        "{\"reviewId\":\"" + existingReviewId + "\",\"status\":\"" + updatedReview.getStatus() + "\"}");

                request.getSession().setAttribute("SUCCESS_MSG", "Đánh giá của bạn đã được cập nhật thành công!");
                redirect(request, response, "/MainController?action=MyReviews");
            } else {
                if (!currentUser.hasPermission("PERM_REVIEW_CREATE")) {
                    redirect(request, response, "/views/shared/accessDenied.jsp");
                    return;
                }
                String reviewId = "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                Review newReview = new Review(reviewId, productId, currentUser.getUserId(), content, rating);

                // ĐIỂM CẬP NHẬT 3: Ủy quyền toàn bộ luồng xử lý cho ReviewService
                reviewService.submitReview(newReview, currentUser, imageStream, extension);

                auditService.logAction(currentUser.getUserId(), "REVIEW_SUBMITTED_CREATE",
                        "{\"reviewId\":\"" + reviewId + "\",\"productId\":\"" + productId + "\",\"status\":\"" + newReview.getStatus() + "\"}");

                if (newReview.getStatus() == domain.enums.ReviewStatus.FLAGGED) {
                    request.getSession().setAttribute("SUCCESS_MSG", "Review submitted but flagged by AI. A moderator will review it.");
                } else if (newReview.getStatus() == domain.enums.ReviewStatus.PUBLISHED) {
                    request.getSession().setAttribute("SUCCESS_MSG", "Thanks! Your review passed AI and is published (may be spot-checked).");
                } else {
                    request.getSession().setAttribute("SUCCESS_MSG", "Thanks! Your review is pending AI analysis.");
                }
                redirect(request, response, "/MainController?action=ViewDetail&id=" + productId);
            }

        } catch (Exception e) {
            request.setAttribute("ERROR", "Lỗi gửi đánh giá: " + e.getMessage());
            forwardToView(request, response, "/views/customer/write-review.jsp");
        }
    }
}

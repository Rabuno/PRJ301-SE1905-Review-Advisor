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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ReviewServlet", urlPatterns = {"/ReviewServlet"})
public class ReviewServlet extends HttpServlet {

    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        // Khởi tạo và cắm (Plug-in) các Module Hạ tầng vào Dịch vụ
        SqlReviewDAO reviewDAO = new SqlReviewDAO();
        WekaProvider aiProvider = new WekaProvider(); // Weka sẽ tự động nạp mô hình ở đây
        this.reviewService = new ReviewService(reviewDAO, aiProvider);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Đảm bảo tiếng Việt không bị lỗi font khi nhận từ form
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            // 1. Lấy thông tin người dùng đang đăng nhập từ Session
            HttpSession session = request.getSession(false);
            User currentUser = (User) session.getAttribute("USER");
            // (AuthFilter đã đảm bảo currentUser luôn tồn tại nếu đến được đây)

            // 2. Trích xuất dữ liệu từ Form HTML
            String productId = request.getParameter("productId");
            String content = request.getParameter("txtContent");
            int rating = Integer.parseInt(request.getParameter("rating"));
            
            // Tạo mã ID duy nhất cho Review (VD: R-A1B2C3D4)
            String reviewId = "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            // 3. Khởi tạo Thực thể (Entity)
            // Lưu ý: Constructor này mặc định gán status là PENDING, 
            // nhưng ReviewService sẽ ghi đè lại sau khi AI chấm điểm.
            Review newReview = new Review(reviewId, productId, currentUser.getUserId(), content, rating);

            // 4. Kích hoạt luồng xử lý
            reviewService.submitReview(newReview);

            // 5. Điều hướng trả về trang chi tiết sản phẩm
            request.getSession().setAttribute("SUCCESS_MSG", "Đánh giá của bạn đã được hệ thống ghi nhận!");
            response.sendRedirect(request.getContextPath() + "/MainController?action=ViewDetail&id=" + productId);

        } catch (Exception e) {
            request.setAttribute("ERROR", "Lỗi gửi đánh giá: " + e.getMessage());
            request.getRequestDispatcher("/views/shared/error.jsp").forward(request, response);
        }
    }
}
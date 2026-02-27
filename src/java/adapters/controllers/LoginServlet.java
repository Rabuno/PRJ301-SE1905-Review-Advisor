package adapters.controllers;

import application.services.AuthService;
import domain.entities.User;
import infrastructure.persistence.SqlUserDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginController"})
public class LoginServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        // Khởi tạo Dependency Injection tĩnh
        this.authService = new AuthService(new SqlUserDAO());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Lấy tham số tương ứng với thuộc tính name trong thẻ input của login.jsp
        String user = request.getParameter("txtUsername");
        String pass = request.getParameter("txtPassword");

        try {
            // 1. Thực thi logic xác thực
            User authenticatedUser = authService.authenticate(user, pass);

            // 2. Thiết lập Session
            HttpSession session = request.getSession();
            session.setAttribute("USER", authenticatedUser);

            // 3. Phân luồng điều hướng dựa trên RoleType
            switch (authenticatedUser.getRole()) {
                case MODERATOR:
                case AUDITOR:
                    // Chuyển hướng đến khu vực quản trị
                    response.sendRedirect(request.getContextPath() + "/views/moderator/dashboard.jsp");
                    break;
                case ADMIN:
                case MERCHANT:
                case CUSTOMER:
                default:
                    // Chuyển hướng đến trang chủ
                    response.sendRedirect(request.getContextPath() + "/views/customer/index.jsp");
                    break;
            }

        } catch (Exception e) {
            // Xác thực thất bại, trả về lỗi cho giao diện
            request.setAttribute("ERROR", e.getMessage());
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
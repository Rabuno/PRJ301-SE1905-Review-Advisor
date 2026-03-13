package adapters.controllers;

import application.services.AuthService;
import domain.entities.User;
import infrastructure.persistence.SqlUserDAO;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        this.authService = new AuthService(new SqlUserDAO());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String user = request.getParameter("txtUsername");
        String pass = request.getParameter("txtPassword");

        try {
            User authenticatedUser = authService.authenticate(user, pass);

            HttpSession session = request.getSession();
            session.setAttribute("USER", authenticatedUser);

            if (authenticatedUser.hasPermission("PERM_MODERATE_ACTION")) {
                response.sendRedirect(request.getContextPath() + "/MainController");
            } else if ("MERCHANT".equals(authenticatedUser.getRole())) {
                response.sendRedirect(request.getContextPath() + "/MerchantServlet");
            } else {
                response.sendRedirect(request.getContextPath() + "/MainController");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Ghi nhận log lỗi phía server để audit

            // Kiểm tra trạng thái an toàn của Response trước khi thao tác
            if (!response.isCommitted()) {
                response.resetBuffer(); // Chỉ dọn dẹp bộ đệm dữ liệu (body), giữ nguyên header
                request.setAttribute("ERROR", "Lỗi xác thực: " + e.getMessage());
                request.getRequestDispatcher("login.jsp").forward(request, response);
            } else {
                // Luồng dữ liệu đã được ghi, không thể thực hiện forward.
                // Bỏ qua việc can thiệp để bảo toàn luồng Chunked Encoding đang gửi.
                getServletContext().log("Critical: Response already committed. Cannot forward exception: ", e);
            }
        }
    }
}

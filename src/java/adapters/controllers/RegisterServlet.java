package adapters.controllers;

import application.services.AuthService;
import infrastructure.persistence.SqlUserDAO;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "RegisterServlet", urlPatterns = { "/RegisterServlet" })
public class RegisterServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        this.authService = new AuthService(new SqlUserDAO());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String user = request.getParameter("txtUsername");
        String pass = request.getParameter("txtPassword");
        String roleType = request.getParameter("roleType");

        boolean isMerchant = "merchant".equalsIgnoreCase(roleType);

        try {
            boolean isRegistered = authService.processRegistration(user, pass, isMerchant);

            if (isRegistered) {
                request.setAttribute("SUCCESS_MSG", "Đăng ký thành công! Vui lòng đăng nhập.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            } else {
                throw new Exception("Lỗi hệ thống khi đăng ký. Vui lòng thử lại sau.");
            }

        } catch (Exception e) {
            request.setAttribute("ERROR", e.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}

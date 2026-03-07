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

        // Nếu người dùng bật công tắc Merchant trên giao diện, value sẽ là "merchant"
        boolean isMerchant = "merchant".equalsIgnoreCase(roleType);

        try {
            boolean isRegistered = authService.processRegistration(user, pass, isMerchant);

            if (isRegistered) {
                // Đã đổi sang tiếng Anh
                request.setAttribute("SUCCESS_MSG", "Registration successful! Please log in.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            } else {
                // Đã đổi sang tiếng Anh
                throw new Exception("System error during registration. Please try again later.");
            }

        } catch (Exception e) {
            request.setAttribute("ERROR", e.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
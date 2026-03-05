package adapters.controllers;

import application.services.AuthService;
import domain.entities.User;
import infrastructure.persistence.SqlUserDAO;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "LoginServlet", urlPatterns = { "/LoginServlet" })
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
                response.sendRedirect(request.getContextPath() + "/ModeratorServlet");
            } else {
                response.sendRedirect(request.getContextPath() + "/MainController");
            }

        } catch (Exception e) {
            request.setAttribute("ERROR", e.getMessage());
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
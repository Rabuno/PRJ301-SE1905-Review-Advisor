package adapters.controllers;

import application.ports.IRoleRepository;
import application.ports.IUserRepository;
import domain.entities.Role;
import domain.entities.User;
import infrastructure.persistence.SqlRoleDAO;
import infrastructure.persistence.SqlUserDAO;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "UserRoleServlet", urlPatterns = { "/Admin/UserRoles" })
public class UserRoleServlet extends HttpServlet {

    private IUserRepository userRepository;
    private IRoleRepository roleRepository;

    @Override
    public void init() throws ServletException {
        this.userRepository = new SqlUserDAO();
        this.roleRepository = new SqlRoleDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Security Check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("USER");
        if (!user.getUsername().equals("admin") && !user.hasPermission("PERM_AI_RETRAIN")) {
            response.sendRedirect(request.getContextPath() + "/views/shared/accessDenied.jsp");
            return;
        }

        List<User> users = userRepository.getAllUsers();
        List<Role> roles = roleRepository.getAllRoles();

        request.setAttribute("users", users);
        request.setAttribute("roles", roles);

        request.getRequestDispatcher("/views/admin/userRoles.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Security Check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("USER");
        if (!user.getUsername().equals("admin") && !user.hasPermission("PERM_AI_RETRAIN")) {
            response.sendRedirect(request.getContextPath() + "/views/shared/accessDenied.jsp");
            return;
        }

        String userId = request.getParameter("userId");
        String roleId = request.getParameter("roleId");

        boolean success = false;
        if (userId != null && !userId.isEmpty() && roleId != null && !roleId.isEmpty()) {
            success = userRepository.updateUserRole(userId, roleId);
        }

        if (success) {
            request.getSession().setAttribute("SUCCESS", "User role updated successfully.");
        } else {
            request.getSession().setAttribute("ERROR", "Failed to update user role.");
        }

        response.sendRedirect(request.getContextPath() + "/Admin/UserRoles");
    }
}

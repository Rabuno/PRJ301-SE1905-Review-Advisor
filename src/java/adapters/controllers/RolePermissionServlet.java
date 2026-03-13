package adapters.controllers;

import application.ports.IRoleRepository;
import domain.entities.Permission;
import domain.entities.Role;
import domain.entities.User;
import infrastructure.persistence.SqlUserDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "RolePermissionServlet", urlPatterns = {"/Admin/RolePermissions"})
public class RolePermissionServlet extends HttpServlet {

    private IRoleRepository roleRepository;

    @Override
    public void init() throws ServletException {
        this.roleRepository = (IRoleRepository) getServletContext().getAttribute("RoleRepository");
        if (this.roleRepository == null) {
            throw new ServletException("Hệ thống chưa nạp được các Service phụ thuộc.");
        }
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
        // Admin-only: changing role permissions is a privileged action.
        if (!"ADMIN".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/views/shared/accessDenied.jsp");
            return;
        }

        List<Role> roles = roleRepository.getAllRoles();
        List<Permission> permissions = roleRepository.getAllPermissions();

        Map<String, List<Integer>> rolePermissionsMap = new HashMap<>();
        for (Role role : roles) {
            rolePermissionsMap.put(role.getRoleId(), roleRepository.getRolePermissions(role.getRoleId()));
        }

        request.setAttribute("roles", roles);
        request.setAttribute("permissions", permissions);
        request.setAttribute("rolePermissionsMap", rolePermissionsMap);

        request.getRequestDispatcher("/views/admin/rolePermissions.jsp").forward(request, response);
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
        if (!"ADMIN".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/views/shared/accessDenied.jsp");
            return;
        }

        String roleIdToUpdate = request.getParameter("roleId");
        String[] permissionIdsStr = request.getParameterValues("permissions_" + roleIdToUpdate);

        List<Integer> permissionIds = new ArrayList<>();
        if (permissionIdsStr != null) {
            for (String pidStr : permissionIdsStr) {
                try {
                    permissionIds.add(Integer.parseInt(pidStr));
                } catch (NumberFormatException e) {
                    // Ignore invalid IDs
                }
            }
        }

        boolean success = roleRepository.updateRolePermissions(roleIdToUpdate, permissionIds);

        if (success) {
            // Permissions are cached by role_id for faster logins; invalidate on change.
            SqlUserDAO.invalidatePermissionCache();
            request.getSession().setAttribute("SUCCESS", "Permissions updated successfully for role.");
        } else {
            request.getSession().setAttribute("ERROR", "Failed to update permissions.");
        }

        response.sendRedirect(request.getContextPath() + "/Admin/RolePermissions");
    }
}

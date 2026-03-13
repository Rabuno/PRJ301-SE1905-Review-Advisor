package infrastructure.security;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import domain.entities.User;

@WebFilter(urlPatterns = { "/*" })
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();

        if (uri.startsWith(contextPath + "/assets") ||
                uri.startsWith(contextPath + "/css") ||
                uri.endsWith("login.jsp") ||
                uri.endsWith("register.jsp") ||
                uri.endsWith("LoginServlet") ||
                uri.endsWith("RegisterServlet")) {

            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("USER") == null) {
            // Public endpoints: view/search/filter/detail on MainController
            if (uri.contains("/MainController")) {
                String action = req.getParameter("action");
                if (action == null
                        || "search".equals(action)
                        || "FilterByCategory".equals(action)
                        || "ViewDetail".equals(action)) {
                    chain.doFilter(request, response);
                    return;
                }
            }
            res.sendRedirect(contextPath + "/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("USER");

        if (uri.contains("/ModeratorServlet") && !user.hasPermission("PERM_MODERATE_ACTION")
                && !"ADMIN".equals(user.getRole())) {
            res.sendRedirect(contextPath + "/views/shared/accessDenied.jsp");
            return;
        }

        if (uri.contains("/AuditServlet") && !user.hasPermission("PERM_AUDIT_READ")
                && !"ADMIN".equals(user.getRole())) {
            res.sendRedirect(contextPath + "/views/shared/accessDenied.jsp");
            return;
        }

        chain.doFilter(request, response);
    }
}

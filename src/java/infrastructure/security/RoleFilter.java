package infrastructure.security;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import domain.entities.User;
import domain.enums.RoleType;

@WebFilter(urlPatterns = {"/views/moderation/*"})
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);

        if (session == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        
        User user = (User) session.getAttribute("USER");

        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        if (user.getRole() != RoleType.MODERATOR) {
    res.sendRedirect(req.getContextPath() + "/views/shared/accessDenied.jsp");
    return;
}

        chain.doFilter(request, response);
    }
}
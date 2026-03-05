package adapters.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseServlet extends HttpServlet {

    protected void forwardToView(HttpServletRequest request, HttpServletResponse response, String viewPath)
            throws ServletException, IOException {
        request.getRequestDispatcher(viewPath).forward(request, response);
    }

    protected void redirect(HttpServletRequest request, HttpServletResponse response, String url)
            throws IOException {
        response.sendRedirect(request.getContextPath() + url);
    }
}

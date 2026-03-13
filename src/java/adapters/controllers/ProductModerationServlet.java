package adapters.controllers;

import application.services.AuditService;
import application.services.ProductService;
import domain.entities.User;
import domain.enums.ProductStatus;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ProductModerationServlet", urlPatterns = {"/ModeratorProducts"})
public class ProductModerationServlet extends BaseServlet {

    private ProductService productService;
    private AuditService auditService;

    @Override
    public void init() throws ServletException {
        this.productService = (ProductService) getServletContext().getAttribute("ProductService");
        this.auditService = (AuditService) getServletContext().getAttribute("AuditService");

        if (this.productService == null || this.auditService == null) {
            throw new ServletException("Hệ thống chưa nạp được các Service phụ thuộc.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("USER");
        if (user == null || (!"ADMIN".equals(user.getRole()) && !user.hasPermission("PERM_MODERATE_ACTION"))) {
            redirect(request, response, "/views/shared/accessDenied.jsp");
            return;
        }

        List<domain.entities.Product> pending = productService.getProductsByStatus(ProductStatus.PENDING);
        request.setAttribute("PENDING_PRODUCTS", pending);
        forwardToView(request, response, "/views/moderation/product-dashboard.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("USER");
        if (user == null || (!"ADMIN".equals(user.getRole()) && !user.hasPermission("PERM_MODERATE_ACTION"))) {
            redirect(request, response, "/views/shared/accessDenied.jsp");
            return;
        }

        String productId = request.getParameter("productId");
        String action = request.getParameter("action");

        try {
            if ("APPROVE".equalsIgnoreCase(action)) {
                productService.updateProductStatus(productId, ProductStatus.ACTIVE);
                auditService.logAction(user.getUserId(), "APPROVE_PRODUCT", "{\"productId\":\"" + productId + "\"}");
                request.getSession().setAttribute("SUCCESS_MSG", "Approved product " + productId);
            } else if ("REJECT".equalsIgnoreCase(action)) {
                productService.updateProductStatus(productId, ProductStatus.DEACTIVATED);
                auditService.logAction(user.getUserId(), "REJECT_PRODUCT", "{\"productId\":\"" + productId + "\"}");
                request.getSession().setAttribute("SUCCESS_MSG", "Rejected product " + productId);
            }
            redirect(request, response, "/ModeratorProducts");
        } catch (Exception e) {
            request.setAttribute("ERROR", "Lỗi xử lý sản phẩm: " + e.getMessage());
            forwardToView(request, response, "/views/shared/error.jsp");
        }
    }
}


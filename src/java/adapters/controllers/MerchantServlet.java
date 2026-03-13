package adapters.controllers;

import application.dto.MerchantStatsDTO;
import application.ports.IFileStoragePort;
import application.services.AuditService;
import application.services.ProductService;
import application.services.ProductTriageService;
import application.services.ReviewService;
import application.dto.AiTriageResult;
import domain.entities.Product;
import domain.entities.Review;
import domain.entities.User;
import domain.enums.ProductStatus;
import infrastructure.storage.LocalFileStorageAdapter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
@WebServlet(name = "MerchantServlet", urlPatterns = { "/MerchantServlet" })
public class MerchantServlet extends HttpServlet {

    private ProductService productService;
    private ReviewService reviewService;
    private ProductTriageService productTriageService;
    private AuditService auditService;

    @Override
    public void init() throws ServletException {
        // Lấy các Singleton Service đã được AppConfigListener khởi tạo
        this.reviewService = (ReviewService) getServletContext().getAttribute("ReviewService");
        this.productService = (ProductService) getServletContext().getAttribute("ProductService");
        this.productTriageService = (ProductTriageService) getServletContext().getAttribute("ProductTriageService");
        this.auditService = (AuditService) getServletContext().getAttribute("AuditService");
        
        if (this.reviewService == null || this.productService == null || this.productTriageService == null || this.auditService == null) {
            throw new ServletException("Hệ thống chưa nạp được các Service phụ thuộc.");
        }
    }

    // ─── Kiểm tra quyền ──────────────────────────────────────────────────────
    private boolean isMerchant(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("USER");
        if (user == null || !"MERCHANT".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/views/shared/accessDenied.jsp");
            return false;
        }
        return true;
    }

    // ─── GET ──────────────────────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isMerchant(request, response))
            return;
        User user = (User) request.getSession().getAttribute("USER");
        String merchantId = user.getUserId();
        String action = request.getParameter("action");

        try {
            if ("ManageProperties".equals(action)) {
                // Manage Properties: list
                List<Product> myProducts = productService.getProductsByMerchant(merchantId);
                request.setAttribute("MERCHANT_PRODUCTS", myProducts);
                request.getRequestDispatcher("/views/merchant/manage-properties.jsp").forward(request, response);

            } else if ("AddProperty".equals(action)) {
                request.getRequestDispatcher("/views/merchant/add-property.jsp").forward(request, response);

            } else if ("EditProperty".equals(action)) {
                String productId = request.getParameter("id");
                // Find within merchant's inventory to include non-ACTIVE statuses.
                Product target = null;
                List<Product> myProducts = productService.getProductsByMerchant(merchantId);
                for (Product p : myProducts) {
                    if (p != null && p.getProductId() != null && p.getProductId().equals(productId)) {
                        target = p;
                        break;
                    }
                }
                if (target == null) {
                    request.setAttribute("ERROR_MSG", "Product not found or not owned by merchant.");
                    request.setAttribute("MERCHANT_PRODUCTS", myProducts);
                    request.getRequestDispatcher("/views/merchant/manage-properties.jsp").forward(request, response);
                    return;
                }
                request.setAttribute("PRODUCT", target);
                request.getRequestDispatcher("/views/merchant/edit-product.jsp").forward(request, response);

            } else {
                // Default: Merchant Dashboard
                int totalProperties = productService.countPropertiesByMerchant(merchantId);
                Object[] statsArray = reviewService.getMerchantReviewStats(merchantId);
                double avgRating = (double) statsArray[0];
                int publishedCount = (int) statsArray[1];
                int flaggedCount = (int) statsArray[2];

                MerchantStatsDTO statsDTO = new MerchantStatsDTO(totalProperties, avgRating, publishedCount,
                        flaggedCount);
                List<Review> recentReviews = reviewService.getRecentMerchantReviews(merchantId, 5);

                // Chart: review sentiment trend (last 7 days)
                int days = 7;
                List<Object[]> trendRows = reviewService.getMerchantReviewTrend(merchantId, days);
                Map<LocalDate, int[]> byDate = new HashMap<>();
                for (Object[] row : trendRows) {
                    if (row == null || row.length < 4) continue;
                    java.sql.Date d = (java.sql.Date) row[0];
                    int pos = ((Number) row[1]).intValue();
                    int neg = ((Number) row[2]).intValue();
                    int flg = ((Number) row[3]).intValue();
                    byDate.put(d.toLocalDate(), new int[]{pos, neg, flg});
                }

                LocalDate start = LocalDate.now().minusDays(days - 1);
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
                List<String> labels = new ArrayList<>();
                List<Integer> positive = new ArrayList<>();
                List<Integer> negative = new ArrayList<>();
                List<Integer> flagged = new ArrayList<>();
                for (int i = 0; i < days; i++) {
                    LocalDate d = start.plusDays(i);
                    labels.add("'" + fmt.format(d) + "'");
                    int[] v = byDate.get(d);
                    positive.add(v == null ? 0 : v[0]);
                    negative.add(v == null ? 0 : v[1]);
                    flagged.add(v == null ? 0 : v[2]);
                }

                request.setAttribute("CHART_LABELS", "[" + String.join(", ", labels) + "]");
                request.setAttribute("CHART_POSITIVE_DATA", positive.toString());
                request.setAttribute("CHART_NEGATIVE_DATA", negative.toString());
                request.setAttribute("CHART_FLAGGED_DATA", flagged.toString());

                request.setAttribute("STATS", statsDTO);
                request.setAttribute("RECENT_FEEDBACK", recentReviews);
                request.getRequestDispatcher("/views/merchant/merchant-dashboard.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("ERROR", "Loi tai du lieu: " + e.getMessage());
            request.getRequestDispatcher("/views/shared/error.jsp").forward(request, response);
        }
    }

    // ─── POST ─────────────────────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isMerchant(request, response))
            return;
        request.setCharacterEncoding("UTF-8");
        User user = (User) request.getSession().getAttribute("USER");
        String merchantId = user.getUserId();
        String action = request.getParameter("action");

        try {
            if ("CreateProperty".equals(action)) {
                if (!user.hasPermission("PERM_PRODUCT_CREATE")) {
                    response.sendRedirect(request.getContextPath() + "/views/shared/accessDenied.jsp");
                    return;
                }
                // ─── Thêm sản phẩm mới ────────────────────────────────────
                String name = request.getParameter("name");
                String category = request.getParameter("category");
                String description = request.getParameter("description");
                double price = Double.parseDouble(request.getParameter("price"));

                // Lưu ảnh nếu có
                String imageUrl = null;
                Part filePart = request.getPart("image");
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = filePart.getSubmittedFileName();
                    String ext = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.')) : "";
                    String uploadPath = request.getServletContext().getRealPath("/assets/uploads");
                    IFileStoragePort storage = new LocalFileStorageAdapter(uploadPath, request.getContextPath());
                    imageUrl = storage.saveFile(filePart.getInputStream(), ext);
                }

                String productId = "PROP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                Product product = new Product(productId, name, category, description, price, merchantId, imageUrl);
                product.setStatus(ProductStatus.PENDING); // always pending for AI + human approval

                AiTriageResult triage = productTriageService.evaluate(product, user);
                boolean flagged = productTriageService.isFlagged(triage);

                boolean ok = productService.createProduct(product);
                if (ok) {
                    if (flagged) {
                        request.getSession().setAttribute("SUCCESS_MSG", "Listing submitted but flagged by AI. Awaiting Moderator/Admin review.");
                        auditService.logAction(user.getUserId(), "PRODUCT_AI_FLAGGED",
                                "{\"productId\":\"" + productId + "\",\"risk\":" + triage.getRiskScore() + "}");
                    } else {
                        request.getSession().setAttribute("SUCCESS_MSG", "Listing submitted. Awaiting AI + Moderator/Admin approval.");
                        auditService.logAction(user.getUserId(), "PRODUCT_SUBMITTED",
                                "{\"productId\":\"" + productId + "\",\"risk\":" + triage.getRiskScore() + "}");
                    }
                } else {
                    request.getSession().setAttribute("ERROR_MSG",
                            "Luu that bai: INSERT vao DB tra ve 0 row. Kiem tra column price/image_url trong DB.");
                }
                response.sendRedirect(request.getContextPath() + "/MerchantServlet?action=ManageProperties");

            } else if ("UpdateProperty".equals(action)) {
                if (!user.hasPermission("PERM_PRODUCT_CREATE")) {
                    response.sendRedirect(request.getContextPath() + "/views/shared/accessDenied.jsp");
                    return;
                }
                // ─── Cập nhật sản phẩm ────────────────────────────────────
                String productId = request.getParameter("productId");
                String name = request.getParameter("name");
                String category = request.getParameter("category");
                String description = request.getParameter("description");
                double price = Double.parseDouble(request.getParameter("price"));

                // Ownership check (prevent guessing productId)
                boolean owns = false;
                for (Product p : productService.getProductsByMerchant(merchantId)) {
                    if (p != null && productId != null && productId.equals(p.getProductId())) {
                        owns = true;
                        break;
                    }
                }
                if (!owns) {
                    response.sendRedirect(request.getContextPath() + "/views/shared/accessDenied.jsp");
                    return;
                }

                String imageUrl = null;
                Part filePart = request.getPart("image");
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = filePart.getSubmittedFileName();
                    String ext = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.')) : "";
                    String uploadPath = request.getServletContext().getRealPath("/assets/uploads");
                    IFileStoragePort storage = new LocalFileStorageAdapter(uploadPath, request.getContextPath());
                    imageUrl = storage.saveFile(filePart.getInputStream(), ext);
                }

                Product product = new Product(productId, name, category, description, price, merchantId, imageUrl);
                product.setStatus(ProductStatus.PENDING); // merchants cannot activate/deactivate directly

                AiTriageResult triage = productTriageService.evaluate(product, user);
                boolean flagged = productTriageService.isFlagged(triage);

                boolean ok = productService.updateProduct(product);
                if (ok) {
                    if (flagged) {
                        request.getSession().setAttribute("SUCCESS_MSG", "Changes submitted but flagged by AI. Awaiting Moderator/Admin review.");
                        auditService.logAction(user.getUserId(), "PRODUCT_UPDATE_AI_FLAGGED",
                                "{\"productId\":\"" + productId + "\",\"risk\":" + triage.getRiskScore() + "}");
                    } else {
                        request.getSession().setAttribute("SUCCESS_MSG", "Changes submitted. Awaiting AI + Moderator/Admin approval.");
                        auditService.logAction(user.getUserId(), "PRODUCT_UPDATED_SUBMITTED",
                                "{\"productId\":\"" + productId + "\",\"risk\":" + triage.getRiskScore() + "}");
                    }
                } else {
                    request.getSession().setAttribute("ERROR_MSG", "Cap nhat that bai, vui long thu lai.");
                }
                response.sendRedirect(request.getContextPath() + "/MerchantServlet?action=ManageProperties");

            } else {
                response.sendRedirect(request.getContextPath() + "/MerchantServlet");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("ERROR_MSG",
                    "Co loi xay ra: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/MerchantServlet?action=ManageProperties");
        }
    }
}

package adapters.controllers;

import application.dto.MerchantStatsDTO;
import application.ports.IAlertRepository;
import application.ports.IFileStoragePort;
import application.ports.IProductRepository;
import application.ports.IReviewRepository;
import application.ports.IUserRepository;
import application.services.ProductService;
import application.services.ReviewService;
import application.services.TriageService;
import domain.entities.Product;
import domain.entities.Review;
import domain.entities.User;
import domain.enums.ProductStatus;
import infrastructure.ai.WekaProvider;
import infrastructure.persistence.SqlAlertDAO;
import infrastructure.persistence.SqlProductDAO;
import infrastructure.persistence.SqlReviewDAO;
import infrastructure.persistence.SqlUserDAO;
import infrastructure.storage.LocalFileStorageAdapter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet(name = "MerchantServlet", urlPatterns = {"/MerchantServlet"})
@MultipartConfig
public class MerchantServlet extends HttpServlet {

    private ProductService productService;
    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        try {
            IReviewRepository reviewDAO = new SqlReviewDAO();
            IUserRepository userDAO = new SqlUserDAO();
            IAlertRepository alertDAO = new SqlAlertDAO();
            IProductRepository productDAO = new SqlProductDAO();

            String uploadDirPath = getServletContext().getRealPath("/assets/uploads");
            IFileStoragePort storagePort = new LocalFileStorageAdapter(uploadDirPath);

            String modelPath = getServletContext().getRealPath("/WEB-INF/model/spam_review_classifier.model");
            TriageService triageService = new TriageService(new WekaProvider(modelPath));

            this.reviewService = new ReviewService(reviewDAO, userDAO, alertDAO, triageService, storagePort);
            this.productService = new ProductService(productDAO);

        } catch (Exception e) {
            throw new ServletException("Lỗi nạp Model tại ModeratorServlet: " + e.getMessage());
        }
    }

    private boolean isMerchant(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("USER");
        if (user == null || !"MERCHANT".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/views/shared/accessDenied.jsp");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isMerchant(request, response)) {
            return;
        }

        User user = (User) request.getSession().getAttribute("USER");
        String merchantId = user.getUserId();
        String action = request.getParameter("action");

        try {
            if ("ManageProperties".equals(action)) {
                List<Product> products = productService.getProductsByMerchant(merchantId);
                request.setAttribute("PROPERTIES", products);
                request.getRequestDispatcher("/views/merchant/manage-properties.jsp").forward(request, response);

            } else {
                int totalProperties = productService.countPropertiesByMerchant(merchantId);
                Object[] statsArray = reviewService.getMerchantReviewStats(merchantId);
                double avgRating = (double) statsArray[0];
                int publishedCount = (int) statsArray[1];
                int flaggedCount = (int) statsArray[2];

                MerchantStatsDTO statsDTO = new MerchantStatsDTO(totalProperties, avgRating, publishedCount, flaggedCount);
                List<Review> recentReviews = reviewService.getRecentMerchantReviews(merchantId, 5);

                request.setAttribute("STATS", statsDTO);
                request.setAttribute("RECENT_FEEDBACK", recentReviews);
                request.getRequestDispatcher("/views/merchant/merchant-dashboard.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("ERROR", "Lỗi tải dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/views/shared/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isMerchant(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");
        User user = (User) request.getSession().getAttribute("USER");
        String merchantId = user.getUserId();
        String action = request.getParameter("action");

        try {
            if ("CreateProperty".equals(action)) {
                String name = request.getParameter("name");
                String category = request.getParameter("category");
                String description = request.getParameter("description");
                double price = Double.parseDouble(request.getParameter("price"));

                String imageUrl = null;
                Part filePart = request.getPart("image");
                if (filePart != null && filePart.getSize() > 0) {
                    IFileStoragePort storage = new LocalFileStorageAdapter(getServletContext().getRealPath("/assets/uploads"));
                    String fileName = filePart.getSubmittedFileName();
                    String ext = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.')) : "";
                    imageUrl = storage.saveFile(filePart.getInputStream(), ext);
                }

                String productId = "PROP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                Product product = new Product(productId, name, category, description, price, merchantId, imageUrl);
                product.setStatus(ProductStatus.PENDING);

                if (productService.createProduct(product)) {
                    request.getSession().setAttribute("SUCCESS_MSG", "Tạo cơ sở dịch vụ thành công!");
                }
                response.sendRedirect(request.getContextPath() + "/MerchantServlet?action=ManageProperties");

            } else if ("UpdateProperty".equals(action)) {
                String productId = request.getParameter("productId");
                String name = request.getParameter("name");
                String category = request.getParameter("category");
                String description = request.getParameter("description");
                double price = Double.parseDouble(request.getParameter("price"));
                String statusStr = request.getParameter("status");

                String imageUrl = request.getParameter("currentImageUrl");
                Part filePart = request.getPart("image");
                if (filePart != null && filePart.getSize() > 0) {
                    IFileStoragePort storage = new LocalFileStorageAdapter(getServletContext().getRealPath("/assets/uploads"));
                    String fileName = filePart.getSubmittedFileName();
                    String ext = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.')) : "";
                    imageUrl = storage.saveFile(filePart.getInputStream(), ext);
                }

                Product product = new Product(productId, name, category, description, price, merchantId, imageUrl);
                if (statusStr != null) {
                    product.setStatus(ProductStatus.valueOf(statusStr));
                }

                productService.updateProduct(product);
                response.sendRedirect(request.getContextPath() + "/MerchantServlet?action=ManageProperties");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/MerchantServlet?action=ManageProperties");
        }
    }
}

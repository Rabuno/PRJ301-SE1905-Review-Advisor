package adapters.controllers;

import application.dto.MerchantStatsDTO;
import application.ports.IAlertRepository;
import application.ports.IReviewRepository;
import application.ports.IUserRepository;
import application.services.ProductService;
import application.services.ReviewService;
import application.services.TriageService;
import domain.entities.Review;
import domain.entities.User;
import infrastructure.ai.WekaProvider;
import infrastructure.persistence.SqlAlertDAO;
import infrastructure.persistence.SqlProductDAO;
import application.ports.IProductRepository;
import infrastructure.persistence.SqlReviewDAO;
import infrastructure.persistence.SqlUserDAO;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import domain.entities.Product;
import application.ports.IFileStoragePort;
import infrastructure.storage.LocalFileStorageAdapter;
import java.io.InputStream;
import java.util.UUID;

@WebServlet(name = "MerchantServlet", urlPatterns = { "/MerchantServlet" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 50)
public class MerchantServlet extends HttpServlet {

    private ProductService productService;
    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        try {
            IReviewRepository reviewDAO = new SqlReviewDAO();
            IUserRepository userDAO = new SqlUserDAO();

            // 1. KỸ THUẬT MOCKING: Giả lập AlertDAO tạm thời để code không báo đỏ chờ DB
            IAlertRepository alertDAO = new SqlAlertDAO(); // Khởi tạo kết nối SQL Server thật

            // Khởi tạo Product DAO và Service
            IProductRepository productDAO = new SqlProductDAO();
            this.productService = new ProductService(productDAO);

            // 2. Định vị tệp Model Weka tự động trên Tomcat (Đã bỏ chữ /classes/)
            String modelPath = getServletContext().getRealPath("/WEB-INF/model/spam_review_classifier.model");
            TriageService triageService = new TriageService(new WekaProvider(modelPath));

            // 3. Khởi tạo ReviewService chính thức (Gán vào biến instance)
            this.reviewService = new ReviewService(reviewDAO, userDAO, alertDAO, triageService);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Lỗi khởi tạo MerchantServlet: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("USER");

        if (user == null || !"MERCHANT".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/views/shared/accessDenied.jsp");
            return;
        }

        try {
            String action = request.getParameter("action");
            String merchantId = user.getUserId();

            if ("ManageProperties".equals(action)) {
                List<Product> products = productService.getProductsByMerchant(merchantId);
                request.setAttribute("PRODUCTS", products);
                request.getRequestDispatcher("/views/merchant/manage-properties.jsp").forward(request, response);
                return;
            }

            if ("AddProperty".equals(action)) {
                // Hien thi form them moi (manage-properties.jsp, khong set PRODUCTS ->
                // c:otherwise block)
                request.getRequestDispatcher("/views/merchant/manage-properties.jsp").forward(request, response);
                return;
            }

            if ("EditProperty".equals(action)) {
                String productId = request.getParameter("productId");
                Product product = productService.getProductById(productId);
                if (product != null && merchantId.equals(product.getMerchantId())) {
                    request.setAttribute("PRODUCT", product);
                    request.getRequestDispatcher("/views/merchant/edit-product.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/MerchantServlet?action=ManageProperties");
                }
                return;
            }

            // Mac dinh: load Merchant Dashboard
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

        } catch (Exception e) {
            request.setAttribute("ERROR", "Loi tai du lieu Dashboard: " + e.getMessage());
            request.getRequestDispatcher("/views/shared/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        User user = (User) request.getSession().getAttribute("USER");

        if (user == null || !"MERCHANT".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/views/shared/accessDenied.jsp");
            return;
        }

        if ("CreateProperty".equals(action)) {
            try {
                String propertyName = request.getParameter("propertyName");
                String category = request.getParameter("category");
                String description = request.getParameter("description");
                String imageUrl = null;

                Part filePart = request.getPart("productImage");
                if (filePart != null && filePart.getSize() > 0) {
                    InputStream imageStream = filePart.getInputStream();
                    String fileName = filePart.getSubmittedFileName();
                    String extension = (fileName != null && fileName.lastIndexOf(".") != -1)
                            ? fileName.substring(fileName.lastIndexOf(".")).toLowerCase()
                            : ".jpg";
                    String uploadDirPath = request.getServletContext().getRealPath("/assets/uploads");
                    IFileStoragePort storage = new LocalFileStorageAdapter(uploadDirPath, request.getContextPath());
                    imageUrl = storage.saveFile(imageStream, extension);
                } else {
                    imageUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&h=600&fit=crop";
                }

                String productId = "P_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                double price = 0.0;
                Product newProduct = new Product(productId, propertyName, description, price, category, "ACTIVE",
                        user.getUserId(), imageUrl);

                boolean isSaved = productService.addProduct(newProduct);
                if (isSaved) {
                    response.sendRedirect(request.getContextPath() + "/MerchantServlet?action=ManageProperties");
                } else {
                    request.setAttribute("ERROR_MSG", "Failed to save the property to the database.");
                    request.getRequestDispatcher("/views/merchant/manage-properties.jsp").forward(request, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("ERROR_MSG", "Error creating property: " + e.getMessage());
                request.getRequestDispatcher("/views/merchant/manage-properties.jsp").forward(request, response);
            }

        } else if ("UpdateProperty".equals(action)) {
            try {
                String productId = request.getParameter("productId");
                String propertyName = request.getParameter("propertyName");
                String category = request.getParameter("category");
                String description = request.getParameter("description");
                String newImageUrl = null;

                Part filePart = request.getPart("productImage");
                if (filePart != null && filePart.getSize() > 0) {
                    // Xoa file anh cu (neu la anh local, khong phai URL ngoai)
                    Product oldProduct = productService.getProductById(productId);
                    if (oldProduct != null && oldProduct.getImageUrl() != null
                            && oldProduct.getImageUrl().startsWith("/assets/uploads/")) {
                        String oldFilePath = request.getServletContext().getRealPath(oldProduct.getImageUrl());
                        java.io.File oldFile = new java.io.File(oldFilePath);
                        if (oldFile.exists()) {
                            oldFile.delete();
                        }
                    }
                    // Luu anh moi
                    InputStream imageStream = filePart.getInputStream();
                    String fileName = filePart.getSubmittedFileName();
                    String extension = (fileName != null && fileName.lastIndexOf(".") != -1)
                            ? fileName.substring(fileName.lastIndexOf(".")).toLowerCase()
                            : ".jpg";
                    String uploadDirPath = request.getServletContext().getRealPath("/assets/uploads");
                    IFileStoragePort storage = new LocalFileStorageAdapter(uploadDirPath, request.getContextPath());
                    newImageUrl = storage.saveFile(imageStream, extension);
                }
                // neu khong upload anh moi thi newImageUrl = null -> update() giu nguyen anh cu

                Product updated = new Product(productId, propertyName, description, 0.0, category, "ACTIVE",
                        user.getUserId(), newImageUrl);
                boolean ok = productService.updateProduct(updated);

                if (ok) {
                    request.getSession().setAttribute("SUCCESS_MSG", "Cap nhat san pham thanh cong!");
                    response.sendRedirect(request.getContextPath() + "/MerchantServlet?action=ManageProperties");
                } else {
                    request.setAttribute("ERROR_MSG", "Khong the cap nhat san pham.");
                    Product p = productService.getProductById(productId);
                    request.setAttribute("PRODUCT", p);
                    request.getRequestDispatcher("/views/merchant/edit-product.jsp").forward(request, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("ERROR_MSG", "Loi khi cap nhat: " + e.getMessage());
                request.getRequestDispatcher("/views/merchant/edit-product.jsp").forward(request, response);
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/MerchantServlet");
        }
    }
}

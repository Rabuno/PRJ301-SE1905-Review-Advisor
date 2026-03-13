package infrastructure.config;

import application.ports.*;
import application.services.*;
import infrastructure.ai.WekaProvider;
import infrastructure.persistence.*;
import infrastructure.storage.LocalFileStorageAdapter;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppConfigListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        try {
            // 1. Khởi tạo các DAO (Data Access Objects)
            IReviewRepository reviewDAO = new SqlReviewDAO();
            IUserRepository userDAO = new SqlUserDAO();
            IAlertRepository alertDAO = new SqlAlertDAO();
            IProductRepository productDAO = new SqlProductDAO();
            IAuditRepository auditDAO = new SqlAuditDAO();

            // 2. Khởi tạo các Adapter hạ tầng
            String uploadDirPath = context.getRealPath("/assets/uploads");
            IFileStoragePort storagePort = new LocalFileStorageAdapter(uploadDirPath);

            String modelPath = context.getRealPath("/WEB-INF/model/spam_review_classifier.model");
            WekaProvider wekaProvider = new WekaProvider(modelPath);

            // 3. Khởi tạo các Service nghiệp vụ (Bơm phụ thuộc)
            TriageService triageService = new TriageService(wekaProvider, alertDAO);
            ReviewService reviewService = new ReviewService(reviewDAO, userDAO, alertDAO, triageService, storagePort);
            ProductService productService = new ProductService(productDAO);
            AuditService auditService = new AuditService(auditDAO);

            // 4. Lưu trữ vào ServletContext (Đăng ký Singleton)
            context.setAttribute("ReviewService", reviewService);
            context.setAttribute("ProductService", productService);
            context.setAttribute("AuditService", auditService);

            System.out.println("[AppConfigListener] Các dịch vụ đã được khởi tạo và đăng ký thành công.");
        } catch (Exception e) {
            System.err.println("[AppConfigListener] Lỗi khởi tạo hệ thống: " + e.getMessage());
            throw new RuntimeException("Không thể khởi động hệ thống do lỗi nạp phụ thuộc.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Giải phóng tài nguyên nếu cần thiết (đóng kết nối pool, v.v.)
    }
}
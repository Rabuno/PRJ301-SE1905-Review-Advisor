package infrastructure.config;

import application.ports.*;
import application.services.*;
import application.ports.IReviewTriageAI;
import application.ports.IProductRecommendationAI;
import infrastructure.ai.ApiProductRecommendationProvider;
import infrastructure.ai.ApiReviewAiProvider;
import infrastructure.ai.FallbackReviewAiProvider;
import infrastructure.ai.HeuristicReviewAiProvider;
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

            // 2.1. Khởi tạo AI Provider (không dùng Weka; ưu tiên API nếu có cấu hình)
            IReviewTriageAI heuristicAI = new HeuristicReviewAiProvider();
            IReviewTriageAI triageAI = heuristicAI;

            String apiUrl = System.getenv("AI_API_URL");
            if (apiUrl != null && !apiUrl.trim().isEmpty()) {
                String apiKey = System.getenv("AI_API_KEY");
                String timeoutMs = System.getenv("AI_API_TIMEOUT_MS");
                int timeout = 10_000;
                try {
                    if (timeoutMs != null && !timeoutMs.trim().isEmpty()) {
                        timeout = Integer.parseInt(timeoutMs.trim());
                    }
                } catch (Exception ignored) {
                }

                IReviewTriageAI apiAI = new ApiReviewAiProvider(apiUrl, apiKey, timeout);
                triageAI = new FallbackReviewAiProvider(apiAI, heuristicAI);
            }

            // 3. Khởi tạo các Service nghiệp vụ (Bơm phụ thuộc)
            TriageService triageService = new TriageService(triageAI);
            ReviewService reviewService = new ReviewService(reviewDAO, userDAO, alertDAO, triageService, storagePort);
            ProductService productService = new ProductService(productDAO);
            AuditService auditService = new AuditService(auditDAO);

            // 3.1 Recommendation Service (API-first, fallback top-rated)
            IProductRecommendationAI recAI = null;
            String recommendUrl = System.getenv("AI_RECOMMEND_URL");
            if (recommendUrl != null && !recommendUrl.trim().isEmpty()) {
                String apiKey = System.getenv("AI_API_KEY");
                String timeoutMs = System.getenv("AI_API_TIMEOUT_MS");
                int timeout = 10_000;
                try {
                    if (timeoutMs != null && !timeoutMs.trim().isEmpty()) {
                        timeout = Integer.parseInt(timeoutMs.trim());
                    }
                } catch (Exception ignored) {
                }
                recAI = new ApiProductRecommendationProvider(recommendUrl, apiKey, timeout);
            }
            RecommendationService recommendationService = new RecommendationService(recAI, reviewService);

            // 4. Lưu trữ vào ServletContext (Đăng ký Singleton)
            context.setAttribute("ReviewService", reviewService);
            context.setAttribute("ProductService", productService);
            context.setAttribute("AuditService", auditService);
            context.setAttribute("RecommendationService", recommendationService);

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

package infrastructure.ai.training;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;
import java.util.ArrayList;

public class ModelTrainer {

    // Đảm bảo thư mục C:/models/ đã được tạo sẵn trên máy tính của bạn
    private static final String MODEL_EXPORT_PATH = "src/java/infrastructure/ai/training/model/spam_review_classifier.model";

    public static void main(String[] args) {
        try {
            System.out.println("[1/4] Khởi tạo không gian vector dữ liệu...");
            
            // 1. Định nghĩa cấu trúc dữ liệu (Phải khớp tuyệt đối với WekaProvider)
            ArrayList<Attribute> attributes = new ArrayList<>();
            attributes.add(new Attribute("review_text", (ArrayList<String>) null)); // Text attribute
            
            ArrayList<String> classLabels = new ArrayList<>();
            classLabels.add("VALID");
            classLabels.add("SPAM");
            attributes.add(new Attribute("class", classLabels));

            Instances trainingData = new Instances("ReviewData", attributes, 10);
            trainingData.setClassIndex(1); // Cột 'class' là nhãn mục tiêu

            System.out.println("[2/4] Chèn dữ liệu mẫu (Dummy Dataset)...");
            // Thêm dữ liệu Hợp lệ (VALID)
            addInstance(trainingData, "Khách sạn rất tuyệt vời, phòng sạch sẽ và nhân viên nhiệt tình.", "VALID");
            addInstance(trainingData, "Đồ ăn ngon, giá cả hợp lý, sẽ quay lại lần sau.", "VALID");
            addInstance(trainingData, "Chất lượng dịch vụ đúng như mong đợi, rất hài lòng.", "VALID");
            
            // Thêm dữ liệu Vi phạm/Giả mạo (SPAM)
            addInstance(trainingData, "Click vào link này để nhận 500k miễn phí http://spam.link", "SPAM");
            addInstance(trainingData, "Kiếm tiền tại nhà chỉ với 2 giờ mỗi ngày, liên hệ zalo 0123456789", "SPAM");
            addInstance(trainingData, "Sản phẩm tệ hại, mọi người đừng mua nhé (đánh giá từ đối thủ).", "SPAM");

            System.out.println("[3/4] Cấu hình thuật toán học máy...");
            // 2. Thiết lập bộ lọc chuyển văn bản thành Vector (TF-IDF)
            StringToWordVector filter = new StringToWordVector();
            filter.setIDFTransform(true);
            filter.setTFTransform(true);
            filter.setLowerCaseTokens(true);

            // 3. Sử dụng FilteredClassifier để bọc NaiveBayes và Filter
            FilteredClassifier classifier = new FilteredClassifier();
            classifier.setFilter(filter);
            classifier.setClassifier(new NaiveBayes());

            // 4. Bắt đầu huấn luyện mô hình
            System.out.println("[Đang huấn luyện...] Quá trình này có thể mất vài giây.");
            classifier.buildClassifier(trainingData);

            System.out.println("[4/4] Đóng gói và xuất mô hình...");
            // Tạo thư mục nếu chưa tồn tại
            File modelFile = new File(MODEL_EXPORT_PATH);
            modelFile.getParentFile().mkdirs();
            
            // 5. Lưu mô hình ra ổ cứng
            SerializationHelper.write(MODEL_EXPORT_PATH, classifier);
            
            System.out.println("✅ THÀNH CÔNG: Mô hình đã được lưu tại " + MODEL_EXPORT_PATH);

        } catch (Exception e) {
            System.err.println("❌ LỖI: Quá trình huấn luyện thất bại!");
            e.printStackTrace();
        }
    }

    // Hàm hỗ trợ chèn dữ liệu vào Instances
    private static void addInstance(Instances data, String text, String label) {
        DenseInstance instance = new DenseInstance(2);
        instance.setDataset(data);
        instance.setValue(data.attribute("review_text"), text);
        instance.setValue(data.attribute("class"), label);
        data.add(instance);
    }
}
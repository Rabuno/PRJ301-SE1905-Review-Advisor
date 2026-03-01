package infrastructure.ai;

import application.ports.IAIService;
import domain.entities.Review;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WekaProvider implements IAIService {

    private static final Logger LOGGER = Logger.getLogger(WekaProvider.class.getName());
    
    private static Classifier spamClassifier;
    private static Instances dataStructure;

    // Sử dụng đường dẫn tương đối nội bộ tính từ thư mục src/java/
    private static final String INTERNAL_MODEL_PATH = "infrastructure/ai/training/model/spam_review_classifier.model"; 

    public WekaProvider() {
        if (spamClassifier == null) {
            loadModelAndStructure();
        }
    }

    private synchronized void loadModelAndStructure() {
        if (spamClassifier != null) return; 
        try {
            // 1. Nạp mô hình thông qua ClassLoader dưới dạng luồng InputStream
            InputStream modelStream = WekaProvider.class.getClassLoader().getResourceAsStream(INTERNAL_MODEL_PATH);
            if (modelStream == null) {
                throw new Exception("Không tìm thấy tệp mô hình tại: " + INTERNAL_MODEL_PATH);
            }
            spamClassifier = (Classifier) SerializationHelper.read(modelStream);
            
            // 2. Thiết lập không gian vector (Vector Space)
            ArrayList<Attribute> attributes = new ArrayList<>();
            attributes.add(new Attribute("review_text", (ArrayList<String>) null)); 
            
            ArrayList<String> classLabels = new ArrayList<>();
            classLabels.add("VALID");
            classLabels.add("SPAM");
            attributes.add(new Attribute("class", classLabels));

            dataStructure = new Instances("ReviewData", attributes, 1);
            dataStructure.setClassIndex(1); 

            LOGGER.info("[AI System] Tải mô hình Weka nội bộ thành công.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[AI System] Lỗi nạp mô hình: ", e);
        }
    }

    @Override
    public double calculateRiskScore(Review review) {
        if (spamClassifier == null || dataStructure == null) {
            return 0.0;
        }

        try {
            DenseInstance newInstance = new DenseInstance(2);
            newInstance.setDataset(dataStructure);
            
            String textContent = review.getContent();
            if (textContent == null || textContent.trim().isEmpty()) {
                return 0.0; 
            }
            newInstance.setValue(dataStructure.attribute("review_text"), textContent);

            double[] probabilities = spamClassifier.distributionForInstance(newInstance);
            return probabilities[1]; // Trả về tỷ lệ SPAM

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[AI System] Lỗi suy luận: ", e);
            return 0.0; 
        }
    }
}
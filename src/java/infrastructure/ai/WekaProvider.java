package infrastructure.ai;

import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import java.util.ArrayList;

public class WekaProvider {
    private FilteredClassifier classifier;
    private Instances dataStructure;

    public WekaProvider(String modelPath) throws Exception {
        this.classifier = (FilteredClassifier) SerializationHelper.read(modelPath);
        setupMultivariateStructure();
    }

    private void setupMultivariateStructure() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("review_text", (ArrayList<String>) null)); 
        attributes.add(new Attribute("rating"));                                
        attributes.add(new Attribute("account_age"));                           
        
        ArrayList<String> classValues = new ArrayList<>();
        classValues.add("ham");  
        classValues.add("spam"); 
        attributes.add(new Attribute("class_label", classValues));              

        this.dataStructure = new Instances("ReviewInference", attributes, 0);
        this.dataStructure.setClassIndex(this.dataStructure.numAttributes() - 1);
    }

    public double calculateRiskScore(String text, double rating, double accountAgeDays) throws Exception {
        DenseInstance instance = new DenseInstance(4);
        instance.setDataset(dataStructure);
        
        instance.setValue(0, text);
        instance.setValue(1, rating);
        instance.setValue(2, accountAgeDays);
        
        // KIỂM TRA LUỒNG DỮ LIỆU (PIPELINE UNIT TEST): In cấu trúc trước khi qua Filter
        System.out.println("System Log - [Weka Input Instance]: " + instance.toString());

        double[] distribution = classifier.distributionForInstance(instance);
        return distribution[1]; 
    }

    // CƠ CHẾ XAI (EXPLAINABLE AI): Trích xuất Top-K bằng Giải thuật Cắt bỏ (Ablation)
    public java.util.List<java.util.Map.Entry<String, Double>> extractTopKRiskFeatures(
            String text, double rating, double accountAgeDays, double baseRiskScore, int k) {
        
        java.util.Map<String, Double> featureImpact = new java.util.HashMap<>();
        
        // 1. Tiền xử lý và tách từ (Tokenization)
        String[] words = text.toLowerCase().replaceAll("[^a-zA-Z0-9à-ỹÀ-Ỹ\\s]", "").split("\\s+");
        java.util.Set<String> uniqueWords = new java.util.HashSet<>(java.util.Arrays.asList(words));

        // 2. Chạy nội suy cắt bỏ (Leave-One-Out Inference)
        for (String word : uniqueWords) {
            if (word.length() < 3) continue; // Bỏ qua từ dừng (Stop-words) quá ngắn

            try {
                // Che lấp từ đang xét (Masking)
                String maskedText = text.replaceAll("(?i)\\b" + word + "\\b", "");
                
                // Dự đoán lại không có từ này
                double newRiskScore = calculateRiskScore(maskedText, rating, accountAgeDays);
                
                // Toán học: Từ này đóng góp bao nhiêu % vào rủi ro? (Nếu gỡ nó ra mà rủi ro giảm, tức là nó có hại)
                double impact = baseRiskScore - newRiskScore;

                if (impact > 0.01) { // Chỉ ghi nhận các từ có trọng số ảnh hưởng > 1%
                    featureImpact.put(word, impact);
                }
            } catch (Exception e) {
                // Bỏ qua lỗi cục bộ để tiếp tục tính toán các từ khác
            }
        }

        // 3. Sắp xếp giảm dần theo trọng số và lấy Top K
        return featureImpact.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(k)
                .collect(java.util.stream.Collectors.toList());
    }
}
package infrastructure.ai.training;

import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.unsupervised.attribute.StringToWordVector;
import java.io.File;

public class ModelTrainer {

    public static void main(String[] args) {
        try {
            // 1. Lấy đường dẫn gốc của dự án NetBeans đang chạy
            String baseDir = System.getProperty("user.dir");

            // 2. Khởi tạo bộ tải dữ liệu vật lý
            CSVLoader loader = new CSVLoader();
            File sourceFile = new File(baseDir + "/web/WEB-INF/dataset/yelp_reviews.csv");

            if (!sourceFile.exists()) {
                System.err.println("LỖI: Không tìm thấy tệp CSV tại " + sourceFile.getAbsolutePath());
                return;
            }

            loader.setSource(sourceFile);
            Instances data = loader.getDataSet();

            // 3. Thiết lập nhãn phân loại ở cột cuối cùng
            if (data.classIndex() == -1) {
                data.setClassIndex(data.numAttributes() - 1);
            }

            // 4. Khởi tạo bộ lọc Không gian Vector (Chỉ lọc cột Text ở Index 0)
            StringToWordVector filter = new StringToWordVector();
            filter.setInputFormat(data);
            filter.setIDFTransform(true);
            filter.setTFTransform(true);
            filter.setLowerCaseTokens(true);
            filter.setAttributeIndices("1");

            // 5. Thiết lập Pipeline lai ghép
            FilteredClassifier classifier = new FilteredClassifier();
            classifier.setFilter(filter);
            classifier.setClassifier(new NaiveBayes());

            // 6. Huấn luyện (Training)
            System.out.println("Đang huấn luyện mô hình đa biến...");
            classifier.buildClassifier(data);

            // 7. Tạo thư mục model (nếu chưa có) và kết xuất
            File modelDir = new File(baseDir + "/web/WEB-INF/model/");
            if (!modelDir.exists()) {
                modelDir.mkdirs();
            }

            String modelPath = modelDir.getAbsolutePath() + "/spam_review_classifier.model";
            weka.core.SerializationHelper.write(modelPath, classifier);

            System.out.println("===========================================");
            System.out.println("THÀNH CÔNG! Đã tạo tệp MODEL mới tại:");
            System.out.println(modelPath);
            System.out.println("===========================================");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

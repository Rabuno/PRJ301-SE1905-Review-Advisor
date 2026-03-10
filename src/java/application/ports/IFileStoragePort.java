package application.ports;

import java.io.InputStream;

public interface IFileStoragePort {
    /**
     * @param fileStream Luồng dữ liệu nhị phân của tệp tin.
     * @param extension Phần mở rộng gốc (ví dụ: .jpg).
     * @return Đường dẫn tương đối (URL) để lưu vào cơ sở dữ liệu.
     */
    String saveFile(InputStream fileStream, String extension) throws Exception;
}
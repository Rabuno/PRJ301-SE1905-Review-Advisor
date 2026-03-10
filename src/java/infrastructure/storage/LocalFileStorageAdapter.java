package infrastructure.storage;

import application.ports.IFileStoragePort;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class LocalFileStorageAdapter implements IFileStoragePort {
    
    private final String uploadDirPath;

    public LocalFileStorageAdapter(String uploadDirPath) {
        this.uploadDirPath = uploadDirPath;
        new File(uploadDirPath).mkdirs(); // Đảm bảo thư mục tồn tại
    }

    @Override
    public String saveFile(InputStream fileStream, String extension) throws Exception {
        String safeFileName = UUID.randomUUID().toString() + extension;
        String absolutePath = uploadDirPath + File.separator + safeFileName;
        
        // Ghi file vật lý
        Files.copy(fileStream, Paths.get(absolutePath), StandardCopyOption.REPLACE_EXISTING);
        
        // Trả về URI tương đối cho DB
        return "/assets/uploads/" + safeFileName; 
    }
}
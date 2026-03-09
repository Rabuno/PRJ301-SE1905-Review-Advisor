package infrastructure.storage;

import application.ports.IStorageService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

public class LocalDiskStorageService implements IStorageService {

    private final String uploadDirectoryPath;
    private final String urlPrefix;

    public LocalDiskStorageService(String uploadDirectoryPath, String urlPrefix) {
        this.uploadDirectoryPath = uploadDirectoryPath;
        this.urlPrefix = urlPrefix;

        // Ensure directory exists
        File uploadDir = new File(uploadDirectoryPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    @Override
    public String saveFile(InputStream fileStream, String fileName) throws Exception {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }

        // Generate a unique filename to prevent collisions
        String fileExtension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            fileExtension = fileName.substring(i);
        }

        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        File targetFile = new File(uploadDirectoryPath + File.separator + uniqueFileName);

        // Copy the stream to the destination file
        try (FileOutputStream outStream = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        }

        // Return the URL path
        return urlPrefix + "/" + uniqueFileName;
    }
}

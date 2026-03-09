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
    private final String contextPath;

    public LocalFileStorageAdapter(String uploadDirPath, String contextPath) {
        this.uploadDirPath = uploadDirPath;
        this.contextPath = (contextPath != null) ? contextPath : "";
        new File(uploadDirPath).mkdirs();
    }

    @Override
    public String saveFile(InputStream fileStream, String extension) throws Exception {
        String safeFileName = UUID.randomUUID().toString() + extension;
        String absolutePath = uploadDirPath + File.separator + safeFileName;

        // Ghi file vat ly
        Files.copy(fileStream, Paths.get(absolutePath), StandardCopyOption.REPLACE_EXISTING);

        // Tra ve URI day du (context path + /assets/uploads/...) de luu vao DB
        return contextPath + "/assets/uploads/" + safeFileName;
    }
}

package application.ports;

import java.io.InputStream;

public interface IStorageService {
    /**
     * Saves a file to the storage medium.
     *
     * @param fileStream The input stream of the file content.
     * @param fileName   The original or generated name of the file.
     * @return The path or URL where the file can be accessed.
     * @throws Exception if an error occurs during saving.
     */
    String saveFile(InputStream fileStream, String fileName) throws Exception;
}

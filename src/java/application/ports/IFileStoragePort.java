package application.ports;

import java.io.InputStream;

public interface IFileStoragePort {
    /**
     * @param fileStream Luong du lieu nhi phan cua tep tin.
     * @param extension  Phan mo rong goc (vi du: .jpg).
     * @return Duong dan tuong doi (URL) de luu vao co so du lieu.
     */
    String saveFile(InputStream fileStream, String extension) throws Exception;
}

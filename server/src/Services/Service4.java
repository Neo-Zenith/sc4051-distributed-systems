package src.Services;

import java.io.File;

public class Service4 {
    private String filePath;

    public Service4(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        File file = new File(filePath);
        if (!file.isFile()) {
            return -1;
        }
        return file.length();
    }
}

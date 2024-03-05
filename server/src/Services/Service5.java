package src.Services;

import java.io.File;

public class Service5 {
    private String filePath;

    public Service5(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean deleteFile() {
        File file = new File(filePath);
        if (!file.isFile()) {
            return false;
        }
        return file.delete();
    }
}

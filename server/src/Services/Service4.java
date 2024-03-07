package src.services;

import java.io.File;

/**
 * Service 4<br>
 * This service will return the size of a file<br>
 * The client will specify the file path and the server will return the size of the file<br>
 * @author Lee Juin
 * @version 1.0
 */
public class Service4 {
    /**
     * The path to the file
     */
    private String filePath;

    /**
     * Constructor for Service 4
     * @param filePath  The path to the file
     */
    public Service4(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Getter method for the file path
     * @return  The file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Setter method for the file path
     * @param filePath  The file path
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Method to get the size of the file
     * @return  The size of the file
     */
    public long getFileSize() {
        File file = new File(filePath);
        if (!file.isFile()) {
            return -1;
        }
        return file.length();
    }
}

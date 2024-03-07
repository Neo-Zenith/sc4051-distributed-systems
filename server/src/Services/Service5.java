package src.services;

import java.io.File;

/**
 * Service 5<br>
 * This service will delete a file<br>
 * The client will specify the file path and the server will delete the file<br>
 * The server will return true if the file is deleted successfully, false otherwise<br>
 * @author Lee Juin
 * @version 1.0
 */
public class Service5 {
    /**
     * The path to the file
     */
    private String filePath;

    /**
     * Constructor for Service 5
     * @param filePath  The path to the file
     */
    public Service5(String filePath) {
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
     * Method to delete the file
     * @return  True if the file is deleted successfully, false otherwise
     */
    public boolean deleteFile() {
        File file = new File(filePath);
        if (!file.isFile()) {
            return false;
        }
        return file.delete();
    }
}

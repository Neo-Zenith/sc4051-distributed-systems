package src.services;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Service 5<br>
 * This service will delete a file<br>
 * The client will specify the file path and the server will delete the file<br>
 * The server will return true if the file is deleted successfully, false
 * otherwise<br>
 * This service also provides a method to delete from file given an offset and
 * number of bytes to delete<br>
 * 
 * @author Lee Juin
 * @version 1.0
 */
public class Service5 {
    /**
     * The path to the file
     */
    private String filePath;

    /**
     * The offset to delete from
     */
    private int offset;

    /**
     * The number of bytes to delete
     */
    private int numBytes;

    /**
     * Constructor for Service 5
     * 
     * @param filePath The path to the file
     */
    public Service5(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Constructor for Service 5
     * 
     * @param filePath The path to the file
     * @param offset   The offset to delete from
     * @param numBytes The number of bytes to delete
     */
    public Service5(String filePath, int offset, int numBytes) {
        this.filePath = filePath;
        this.offset = offset;
        this.numBytes = numBytes;
    }

    /**
     * Getter method for the file path
     * 
     * @return The file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Getter method for the offset
     * 
     * @return The offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Getter method for the num of bytes to delete
     * 
     * @return The num of bytes to delete
     */
    public int getNumBytes() {
        return numBytes;
    }

    /**
     * Setter method for the file path
     * 
     * @param filePath The file path
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Setter method for the offset
     * 
     * @param offset The offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Setter method for the num of bytes to delete
     * 
     * @param numBytes The num of bytes to delete
     */
    public void setNumBytes(int numBytes) {
        this.numBytes = numBytes;
    }

    /**
     * Method to delete the file
     * 
     * @return True if the file is deleted successfully, false otherwise
     */
    public boolean deleteFile() {
        File file = new File(filePath);
        if (!file.isFile()) {
            return false;
        }
        return file.delete();
    }

    /**
     * Method to delete from file given an offset and number of bytes to delete
     * 
     * @param offset   The offset in the file
     * @param numBytes The number of bytes to delete
     * @return True if the bytes are deleted successfully, false otherwise
     */
    public int deleteFromFile() {
        try {
            File fileObj = new File(filePath);
            if (!fileObj.isFile()) {
                return 404;
            }
            long fileLength = fileObj.length();
            if (offset < 0 || offset >= fileLength || numBytes <= 0 || offset + numBytes > fileLength) {
                return 400;
            }
            RandomAccessFile file = new RandomAccessFile(filePath, "rw");
            byte[] remainingBytes = new byte[(int) (fileLength - offset - numBytes)];
            file.seek(offset + numBytes);
            file.read(remainingBytes);
            file.seek(offset);
            file.write(remainingBytes);
            file.setLength(fileLength - numBytes);
            return 200;
        } catch (IOException e) {
            return 404;
        }
    }

}

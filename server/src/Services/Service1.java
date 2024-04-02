package src.services;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Service 1<br>
 * This service will read a file from the server and send it to the client<br>
 * The client will specify the file path, offset and number of bytes to read<br>
 * The server will read the file and send the requested bytes to the client<br>
 * @author Lee Juin
 * @version 1.0
 */
public class Service1 {
    /**
     * The path to the file
     */
    private String filePath;
    /**
     * The offset to start reading from
     */
    private int offset;
    /**
     * The number of bytes to read
     */
    private int numBytes;

    /**
     * Constructor for Service 1
     * @param filePath  The path to the file
     * @param offset    The offset to start reading from
     * @param numBytes  The number of bytes to read
     */
    public Service1(String filePath, int offset, int numBytes) {
        this.filePath = filePath;
        this.offset = offset;
        this.numBytes = numBytes;
    }

    /**
     * Constructor for Service 1 to read the entire file
     * @param filePath  The path to the file
     */
    public Service1(String filePath) {
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
     * Getter method for the offset
     * @return  The offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Getter method for the number of bytes
     * @return  The number of bytes
     */
    public int getNumBytes() {
        return numBytes;
    }

    /**
     * Setter method for the file path
     * @param filePath The file path
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Setter method for the offset
     * @param offset    The offset
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Setter method for the number of bytes
     * @param numBytes  The number of bytes
     */
    public void setNumBytes(int numBytes) {
        this.numBytes = numBytes;
    }

    /**
     * Read the file from the server
     * @return  The bytes read from the file
     */
    public String readFromFile() {
        try {
            RandomAccessFile file = new RandomAccessFile(filePath, "r");
            if (file.length() < offset) {
                file.close();
                return "";
            }
            file.seek(offset);
            byte[] buffer = new byte[numBytes > file.length() - offset ? (int) (file.length() - offset) : numBytes];
            file.read(buffer);
            file.close();
            return new String(buffer);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Read the last updated timestamp of the file
     * @return  The last updated timestamp of the file
     */
    public long readFileLastUpdatedTimestamp() {
        Path filePath = Paths.get(this.filePath);
        try {
            BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);
            return attr.lastModifiedTime().toMillis();
        } catch (IOException e) {
            System.err.println("Unable to read file attributes: " + e.getMessage());
            return -1;
        }             
    }

    /**
     * Read the entire file from the server
     * @return  The entire file
     */
    public String readFullFile() {
        try {
            RandomAccessFile file = new RandomAccessFile(filePath, "r");
            byte[] buffer = new byte[(int) file.length()];
            file.read(buffer);
            file.close();
            return new String(buffer);
        } catch (IOException e) {
            return null;
        }
    }
}

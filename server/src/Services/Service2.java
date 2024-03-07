package src.services;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;

/**
 * Service 2<br>
 * This service will insert bytes into a file at a specified offset<br>
 * The client will specify the file path, offset and the bytes to insert<br>
 * The server will insert the bytes into the file at the specified offset<br>
 * @author Lee Juin
 * @version 1.0
 */
public class Service2 {
    /**
     * The path to the file
     */
    private String filePath;
    /**
     * The offset to start reading from
     */
    private int offset;
    /**
     * The bytes to insert
     */
    private byte[] bytesToInsert;

    /**
     * Constructor for Service 2
     * @param filePath      The path to the file
     * @param offset        The offset to start reading from
     * @param bytesToInsert The bytes to insert
     */
    public Service2(String filePath, int offset, byte[] bytesToInsert) {
        this.filePath = filePath;
        this.offset = offset;
        this.bytesToInsert = bytesToInsert;
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
     * Getter method for the bytes to insert
     * @return  The bytes to insert
     */
    public byte[] getBytesToInsert() {
        return bytesToInsert;
    }

    /**
     * Setter method for the file path
     * @param filePath  The file path
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
     * Setter method for the bytes to insert
     * @param bytesToInsert The bytes to insert
     */
    public void setBytesToInsert(byte[] bytesToInsert) {
        this.bytesToInsert = bytesToInsert;
    }

    /**
     * Method to write the bytes to the file at the specified offset
     * @return  The status code
     */
    public int writeToFile() {
        try {
            File fileObj = new File(filePath);
            if (!fileObj.isFile()) {
                return 404;
            }
            long fileLength = fileObj.length();
            if (offset > fileLength) {
                return 400;
            }
            RandomAccessFile file = new RandomAccessFile(filePath, "rw");
            byte[] existingContent = new byte[(int) (fileLength - offset)];
            file.seek(offset);
            file.read(existingContent);
            file.seek(offset);
            file.write(bytesToInsert);
            file.write(existingContent);
            file.close();
            return 200;
        } catch (IOException e) {
            return 404;
        }
    }
}

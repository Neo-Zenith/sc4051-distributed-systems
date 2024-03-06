package src.Services;

import java.io.RandomAccessFile;
import java.io.IOException;

/**
 * Service 1
 * This service will read a file from the server and send it to the client
 * The client will specify the file path, offset and number of bytes to read
 * The server will read the file and send the requested bytes to the client
 */
public class Service1 {
    private String filePath;
    private int offset;
    private int numBytes;

    public Service1(String filePath, int offset, int numBytes) {
        this.filePath = filePath;
        this.offset = offset;
        this.numBytes = numBytes;
    }

    public Service1(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getOffset() {
        return offset;
    }

    public int getNumBytes() {
        return numBytes;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setNumBytes(int numBytes) {
        this.numBytes = numBytes;
    }

    public String readFromFile() {
        try {
            RandomAccessFile file = new RandomAccessFile(filePath, "r");
            file.seek(offset);
            byte[] buffer = new byte[numBytes];
            file.read(buffer);
            file.close();
            return new String(buffer);
        } catch (IOException e) {
            return null;
        }
    }

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

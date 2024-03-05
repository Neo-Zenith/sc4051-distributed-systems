package src.Services;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;

public class Service2 {
    private String filePath;
    private int offset;
    private byte[] bytesToInsert;

    public Service2(String filePath, int offset, byte[] bytesToInsert) {
        this.filePath = filePath;
        this.offset = offset;
        this.bytesToInsert = bytesToInsert;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getOffset() {
        return offset;
    }

    public byte[] getBytesToInsert() {
        return bytesToInsert;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setBytesToInsert(byte[] bytesToInsert) {
        this.bytesToInsert = bytesToInsert;
    }

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

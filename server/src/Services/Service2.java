package src.Services;

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

    //TODO: Implement writeToFile error logic, this is just default template
    public void writeToFile() {
        try {
            java.io.RandomAccessFile file = new java.io.RandomAccessFile(filePath, "rw");
            file.seek(offset);
            file.write(bytesToInsert);
            file.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}

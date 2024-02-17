package src.Marshaller;

public class ClientInput {
    private int requestID;
    private int serviceID;
    private String filePath;
    private int offset;
    private int numBytes;
    private byte[] bytesToInsert;

    /**
     * Constructor for InputFormat for Service 1
     * @param requestID request ID
     * @param serviceID service ID
     * @param filePath  path to file
     * @param offset    offset to start reading from
     * @param numBytes  number of bytes to read
     */
    public ClientInput(int requestID, int serviceID, String filePath, int offset, int numBytes) {
        this.requestID = requestID;
        this.serviceID = serviceID;
        this.filePath = filePath;
        this.offset = offset;
        this.numBytes = numBytes;
    }

    /**
     * Constructor for InputFormat for Service 2
     * @param requestID         request ID
     * @param serviceID         service ID
     * @param filePath          path to file
     * @param offset            offset to start reading from
     * @param bytesToInsert     bytes to insert
     */
    public ClientInput(int requestID, int serviceID, String filePath, int offset, byte[] bytesToInsert) {
        this.filePath = filePath;
        this.serviceID = serviceID;
        this.offset = offset;
        this.bytesToInsert = bytesToInsert;
    }

    public int getRequestID() {
        return requestID;
    }

    public int getServiceID() {
        return serviceID;
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

    public byte[] getBytesToInsert() {
        return bytesToInsert;
    }
}

package src.Marshaller;

public class ClientPayload {
    private int offset;
    private int numBytes;
    private byte[] bytesToInsert;

    /**
     * Constructor for the payload for Service 1
     * @param offset    offset to start reading from
     * @param numBytes  number of bytes to read
     */
    public ClientPayload(int offset, int numBytes) {
        this.offset = offset;
        this.numBytes = numBytes;
    }

    /**
     * Constructor for the payload for Service 2
     * @param offset            offset to start reading from
     * @param bytesToInsert     bytes to insert
     */
    public ClientPayload(int offset, byte[] bytesToInsert) {
        this.offset = offset;
        this.bytesToInsert = bytesToInsert;
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

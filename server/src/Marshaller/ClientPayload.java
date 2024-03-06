package src.Marshaller;

import java.util.Date;

public class ClientPayload {
    private int offset;
    private int numBytes;
    private byte[] bytesToInsert;
    private Date expiryDate;

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

    /**
     * Constructor for the payload for Service 3
     * @param expiryDate    expiry date for monitoring
     */
    public ClientPayload(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * Default constructor
     */
    public ClientPayload() {}

    public int getOffset() {
        return offset;
    }

    public int getNumBytes() {
        return numBytes;
    }

    public byte[] getBytesToInsert() {
        return bytesToInsert;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }
}

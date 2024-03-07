package src.marshaller;

import java.util.Date;

/**
 * Class to store the payload for the client's request specific for a service
 */
public class ClientPayload {
    /**
     * The offset to start reading from (only for Service 1 and 2)
     */
    private int offset;
    /**
     * The number of bytes to read (only for Service 1)
     */
    private int numBytes;
    /**
     * The bytes to insert (only for Service 2)
     */
    private byte[] bytesToInsert;
    /**
     * The expiry date for monitoring (only for Service 3)
     */
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
     * Default constructor for service 4 and 5
     */
    public ClientPayload() {}

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
     * Getter method for the bytes to insert
     * @return  The bytes to insert
     */
    public byte[] getBytesToInsert() {
        return bytesToInsert;
    }

    /**
     * Getter method for the expiry date
     * @return  The expiry date
     */
    public Date getExpiryDate() {
        return expiryDate;
    }
}

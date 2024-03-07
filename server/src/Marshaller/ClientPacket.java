package src.marshaller;

/**
 * Class to store a client's request
 * @author Lee Juin
 * @version 1.0
 */
public class ClientPacket {
    /**
     * The request ID of the client
     */
    private int requestID;
    /**
     * The service ID of the client
     */
    private int serviceID;
    /**
     * The length of the request
     */
    private int requestLength;
    /**
     * The path to the file
     */
    private String filePath;
    /**
     * The payload for the service
     */
    private ClientPayload clientPayload;

    /**
     * Constructor for Client Packet
     * @param requestID request ID
     * @param serviceID service ID
     * @param filePath  path to file
     * @param clientPayload payload for the service
     */
    public ClientPacket(int requestID, int serviceID, String filePath, ClientPayload clientPayload) {
        this.requestID = requestID;
        this.serviceID = serviceID;
        this.filePath = filePath;
        this.clientPayload = clientPayload;
    }

    /**
     * Get the request ID
     * @return  The request ID
     */
    public int getRequestID() {
        return this.requestID;
    }

    /**
     * Get the request length
     * @return  The request length
     */
    public int getRequestLength() {
        return this.requestLength;
    }

    /**
     * Get the service ID
     * @return  The service ID
     */
    public int getServiceID() {
        return this.serviceID;
    }

    /**
     * Get the file path
     * @return  The file path
     */
    public String getFilePath() {
        return this.filePath;
    }

    /**
     * Get the client payload
     * @return  The client payload
     */
    public ClientPayload getClientPayload() {
        return this.clientPayload;
    }
}

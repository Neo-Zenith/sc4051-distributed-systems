package src.Marshaller;

public class ClientPacket {
    private int requestID;
    private int serviceID;
    private int requestLength;
    private String filePath;
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

    public int getRequestID() {
        return this.requestID;
    }

    public int getRequestLength() {
        return this.requestLength;
    }

    public int getServiceID() {
        return this.serviceID;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public ClientPayload getClientPayload() {
        return this.clientPayload;
    }
}

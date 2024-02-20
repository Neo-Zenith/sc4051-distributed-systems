package src.Marshaller;

/** Server Input format
 * [0:3] - Response ID
 * [4] - Status code to indicate error or success
 * [5:8] - Length of the response
 * [9:...] - Response content
 */
public class ServerPacket {
    private int responseID;
    private boolean status;
    private int responseLength;

    public ServerPacket(int responseID, boolean status, int responseLength) {
        this.responseID = responseID;
        this.status = status;
        this.responseLength = responseLength;
    }

    public int getResponseID() {
        return responseID;
    }

    public boolean getStatus() {
        return status;
    }

    public int getResponseLength() {
        return responseLength;
    }
}

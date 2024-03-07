package src.comms;
import java.net.InetAddress;

/**
 * Class to store the details of a client
 * This includes the IP address and port number
 * @author Lee Juin
 * @version 1.0
 */
public class ClientDetails {
    /**
     * The IP address of the client
     */
    private InetAddress address;
    /**
     * The port number of the client
     */
    private int port;

    /**
     * Constructor for the ClientDetails class
     * @param address The IP address of the client
     * @param port The port number of the client
     */
    public ClientDetails(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    /**
     * Getter method for the IP address of the client
     * @return  The IP address of the client
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Getter method for the port number of the client
     * @return  The port number of the client
     */
    public int getPort() {
        return port;
    }

    /**
     * Setter method for the IP address of the client
     * @param address   The IP address of the client
     */
    public void setAddress(InetAddress address) {
        this.address = address;
    }

    /**
     * Setter method for the port number of the client
     * @param port  The port number of the client
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Method to return the string representation of the client details<br>
     * To be used to hash the client details (used in hashMap key put and retrieval)
     * @return  The string representation of the client details
     */
    @Override
    public int hashCode() {
        return address.hashCode() + port;
    }

    /**
     * Method to check if the client details are the same
     * @param obj   The object to compare with
     * @return  True if the client details are the same, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ClientDetails)) {
            return false;
        }
        ClientDetails clientDetails = (ClientDetails) obj;
        return clientDetails.getAddress().equals(address) && clientDetails.getPort() == port;
    }
}

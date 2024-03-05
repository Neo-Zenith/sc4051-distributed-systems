package src.Comms;
import java.net.InetAddress;

public class ClientDetails {
    private InetAddress address;
    private int port;

    public ClientDetails(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int hashCode() {
        return address.hashCode() + port;
    }

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

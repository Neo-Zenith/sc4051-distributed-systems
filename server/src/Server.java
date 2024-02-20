package src;
import java.net.SocketException;
import java.io.IOException;
import java.net.*;

import src.Comms.ClientDetails;
import src.Controller.Controller;
import src.Marshaller.ClientPacket;
import src.Marshaller.Marshaller;

public class Server {
    static DatagramSocket socket = null; // initialise the server socket
    static int port = 2222;

    public static void main(String[] args) {
        try {
            socket = new DatagramSocket(port);
        }
        catch (SocketException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        Server.start();
    }

    public static void start() {
        // Initial buffer window size
        int bufferWindowSize = 12;
        try {
            while (true) {
                System.out.println("Waiting for response...");
                // buffer for storing the received request from client
                // start at 8 bytes.
                byte[] buffer = new byte[bufferWindowSize];
                // DatagramPack encapsulates the request message sent by client (to be stored in buffer)
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request); 
                System.out.println("Received request from client");

                // unmarshal the request
                ClientPacket clientPacket = Marshaller.unmarshalClientPacket(buffer);

                if (clientPacket.getServiceID() == 0) {
                    // if request ID is 0, then it is the first packet
                    // set the buffer window size and listen for next packet
                    System.out.println("================ Header packet ================");
                    System.out.println("Request ID: " + clientPacket.getRequestID());
                    System.out.println("Service ID: " + clientPacket.getServiceID());
                    System.out.println("Request Length: " + clientPacket.getRequestLength());
                    bufferWindowSize = clientPacket.getRequestLength();
                    System.out.println("Buffer window size set to " + bufferWindowSize);
                    byte[] replyBuffer = new byte["OK".getBytes().length];
                    replyBuffer = "OK".getBytes();
                    Server.sendReply(request, replyBuffer);
                    System.out.println("===============================================");
                } else {
                    // if request ID is not 0, then it is the second packet
                    // need to get the rest of the data
                    System.out.println("================ Data packet ================");
                    buffer = new byte[clientPacket.getRequestLength()];
                    request = new DatagramPacket(buffer, buffer.length);
                    socket.receive(request);
                    clientPacket = Marshaller.unmarshalClientPacket(buffer);
                    Controller.processRequest(clientPacket);
                    System.out.println("===============================================");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    /**
     * Get the client details from the request
     * @param request   DatagramPacket encapsulating the request message
     * @return ClientDetails object containing the client's address and port
     */
    public static ClientDetails getClientDetails(DatagramPacket request) {
        InetAddress clientAddr = request.getAddress();
        int clientPort = request.getPort();
        return new ClientDetails(clientAddr, clientPort);
    }

    /**
     * Send the reply to the client
     * @param clientDetails ClientDetails object containing the client's address and port
     * @param replyBuffer   Buffer containing the reply message
     */
    public static void sendReply(DatagramPacket request, byte[] replyBuffer) {
        try {
            ClientDetails clientDetails = Server.getClientDetails(request);
            DatagramPacket reply = new DatagramPacket(replyBuffer, replyBuffer.length, clientDetails.getAddress(), clientDetails.getPort());
            socket.send(reply);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
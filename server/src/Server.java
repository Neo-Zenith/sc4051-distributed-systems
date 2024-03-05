package src;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;

import src.Comms.ClientDetails;
import src.Controller.Controller;
import src.Marshaller.ClientPacket;
import src.Marshaller.Marshaller;

public class Server {
    private static DatagramSocket socket = null; // Initialise the server socket
    private static int port = 2222;
    // A map of client to their latest request ID
    private static HashMap<ClientDetails, Integer> requests = new HashMap<ClientDetails, Integer>();

    public static void main(String[] args) {
        // Creates the socket for the server
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        Server.start();
    }

    /**
     * Start the server
     */
    public static void start() {
        try {
            while (true) {
                System.out.println("Waiting for response...");
                // Buffer to store the request from the client
                // Assume buffer never overflows, so set to MAX
                byte[] buffer = new byte[Integer.MAX_VALUE];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                ClientDetails clientDetails = Server.getClientDetails(request);
                System.out.println(
                        "Received content from client " + clientDetails.getAddress() + ":" + clientDetails.getPort());

                // Unmarshal the request
                ClientPacket clientPacket = Marshaller.unmarshalClientPacket(buffer);
                System.out.println("================ Data packet ================");
                System.out.println("Request ID: " + clientPacket.getRequestID());
                Controller.processRequest(request, clientPacket);
                System.out.println("=============================================");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    /**
     * Get the client details from the request
     * 
     * @param request DatagramPacket encapsulating the request message
     * @return ClientDetails object containing the client's address and port
     */
    public static ClientDetails getClientDetails(DatagramPacket request) {
        InetAddress clientAddr = request.getAddress();
        int clientPort = request.getPort();
        return new ClientDetails(clientAddr, clientPort);
    }

    /**
     * Send a response to the client
     * 
     * @param clientDetails ClientDetails object containing the client's address and
     *                      port
     * @param replyBuffer   Buffer containing the response data
     */
    public static void sendReply(DatagramPacket request, byte[] replyBuffer) {
        try {
            System.out.println("-----------------------------");
            ClientDetails clientDetails = Server.getClientDetails(request);
            DatagramPacket reply = new DatagramPacket(replyBuffer, replyBuffer.length, clientDetails.getAddress(),
                    clientDetails.getPort());
            socket.send(reply);
            System.out.println("Acknowledgement sent");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the map of clients to their latest request ID
     * @return  HashMap of ClientDetails to their latest request ID
     */
    public static HashMap<ClientDetails, Integer> getRequests() {
        return Server.requests;
    }

    /**
     * Check if the request is duplicated
     * @param clientDetails     ClientDetails object containing the client's address and port
     * @param requestID         Request ID
     * @return   True if the request is duplicated, false otherwise
     */
    public boolean isRequestDuplicated(ClientDetails clientDetails, int requestID) {
        if (Server.requests.containsKey(clientDetails) && Server.requests.get(clientDetails) == requestID) {
            return true;
        }
        return false;
    }

    /**
     * Update the request ID for the client
     * @param clientDetails     ClientDetails object containing the client's address and port
     * @param requestID         Request ID
     */
    public static void updateRequest(ClientDetails clientDetails, int requestID) {
        Server.requests.put(clientDetails, requestID);
    }
}
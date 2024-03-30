package src;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;

import src.controller.Controller;
import src.marshaller.ClientPacket;
import src.marshaller.Marshaller;
import src.comms.ClientDetails;

/**
 * Server<br>
 * The server will receive requests from the client and process them accordingly<br>
 * The server will then send a response back to the client<br>
 * @author  Lee Juin
 * @version 1.0
 */
public class Server {
    /**
     * Enum to specify the timeout frequency
     */
    public enum TimeoutFrequency {
        /**
         * Timeout every 2 requests
         */
        EVERY_2_REQUESTS,
        /**
         * Timeout every 4 requests
         */
        EVERY_4_REQUESTS,
        /**
         * Random timeout of 10% probability
         */
        RANDOM,
        /**
         * Never timeout
         */
        NEVER
    }

    /**
     * The server socket
     */
    private static DatagramSocket socket = null; // Initialise the server socket
    
    /**
     * The port number
     */
    private static int port = 2222;
    /** 
     * A map of client to their latest request ID*
     */
    private static HashMap<ClientDetails, Integer> requests = new HashMap<ClientDetails, Integer>();
    /**
     * Store reply messages for at-most-once semantics
     */
    private static HashMap<ClientDetails, HashMap<Integer, byte[]>> replyMessages = new HashMap<ClientDetails, HashMap<Integer, byte[]>>();

    /**
     * Main method to start the server
     * @param args  Command line arguments
     */
    public static void main(String[] args) {
        // Check if we are using at-most-once semantics or at-least-once semantics
        // First argument is the semantics
        // Second argument is the timeout frequency
        System.out.println("=============================================");
        System.out.println("Server started");
        if (args.length > 1) {
            if (args[0].equals("at-most-once")) {
                System.out.println("Using at-most-once semantics");
                Controller.setAtMostOnce(true);
            } else if (args[0].equals("at-least-once")) {
                System.out.println("Using at-least-once semantics");
                Controller.setAtMostOnce(false);
            } else {
                System.out.println("Invalid argument. Usage: java Server [at-most-once|at-least-once] [every-2-requests|every-4-requests|never|random]");
                System.exit(-1);
            }

            if (args[1].equals("every-2-requests")) {
                System.out.println("Using timeout frequency: every 2 requests");
                Controller.setTimeoutFrequency(TimeoutFrequency.EVERY_2_REQUESTS);
            } else if (args[1].equals("every-4-requests")) {
                System.out.println("Using timeout frequency: every 4 requests");
                Controller.setTimeoutFrequency(TimeoutFrequency.EVERY_4_REQUESTS);
            } else if (args[1].equals("never")) {
                System.out.println("Using timeout frequency: never");
                Controller.setTimeoutFrequency(TimeoutFrequency.NEVER);
            } else if (args[1].equals("random")) {
                System.out.println("Using timeout frequency: random");
                Controller.setTimeoutFrequency(TimeoutFrequency.RANDOM);
            } else {
                System.out.println("Invalid argument. Usage: java Server [at-most-once|at-least-once] [every-2-requests|every-4-requests|never|random]");
                System.exit(-1);
            }
        } else {
            System.out.println("Insufficient arguments provided. Using at-most-once semantics by default and timeout frequency: never");
            Controller.setAtMostOnce(true);
            Controller.setTimeoutFrequency(TimeoutFrequency.NEVER);
        }
        System.out.println("=============================================");

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
                // Assume buffer never overflows, set to 65536 bytes
                // Integer.MAX_VALUE = 2147483647 causes OutofMemoryError
                byte[] buffer = new byte[65536];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                ClientDetails clientDetails = Server.getClientDetails(request);
                System.out.println("=============================================");
                System.out.println(
                        "Received request from client " + clientDetails.getAddress() + ":" + clientDetails.getPort());

                // Unmarshal the request
                ClientPacket clientPacket = Marshaller.unmarshalClientPacket(buffer);
                System.out.println("--------------- Request packet --------------");
                System.out.println("Request ID: " + clientPacket.getRequestID());
                System.out.println("Service ID: " + clientPacket.getServiceID());
                // Add request to the map
                Server.updateRequest(clientDetails, clientPacket.getRequestID());
                Controller.processRequest(request, clientPacket);
                System.out.println("=============================================\n");
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
     * @param requestID     Request ID
     * @param request       DatagramPacket encapsulating the client details
     * @param replyBuffer   Buffer containing the response data
     * @param storeReply    Boolean to indicate if the reply should be stored
     */
    public static void sendReply(int requestID, DatagramPacket request, byte[] replyBuffer, boolean storeReply) {
        try {
            ClientDetails clientDetails = Server.getClientDetails(request);
            if (storeReply)
                Server.addReplyMessage(clientDetails, requestID, replyBuffer);
            System.out.println("Stored reply for client: " + clientDetails.getAddress() + ":" + clientDetails.getPort());
            // Simulate reply message lost from server to client
            if (Controller.shouldTimeout()) {
                System.out.println("Simulating reply message lost from server to client");
                System.out.println("Reply not sent");
                return;
            }
            
            DatagramPacket reply = new DatagramPacket(replyBuffer, replyBuffer.length, clientDetails.getAddress(),
                    clientDetails.getPort());
            socket.send(reply);
            System.out.println("Reply sent");
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

    /**
     * Get the reply messages for the client
     * @param clientDetails     ClientDetails object containing the client's address and port
     * @return  HashMap of request ID to reply message
     */
    public static HashMap<Integer, byte[]> getReplyMessages(ClientDetails clientDetails) {
        return Server.replyMessages.get(clientDetails);
    }

    /**
     * Add the reply message to the map
     * @param clientDetails     ClientDetails object containing the client's address and port
     * @param requestID         Request ID
     * @param replyMessage      Reply message
     */
    public static void addReplyMessage(ClientDetails clientDetails, int requestID, byte[] replyMessage) {
        if (!Server.replyMessages.containsKey(clientDetails)) {
            Server.replyMessages.put(clientDetails, new HashMap<Integer, byte[]>());
        }
        Server.replyMessages.get(clientDetails).put(requestID, replyMessage);
    }
}
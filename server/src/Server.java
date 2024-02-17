package src;
import java.net.SocketException;
import java.io.IOException;
import java.net.*;

import src.Comms.ClientDetails;
import src.Controller.Controller;
import src.Marshaller.ClientInput;
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
        try {
            while (true) {
                // buffer for storing the received request from client
                byte[] buffer = new byte[512];
                // DatagramPack encapsulates the request message sent by client (to be stored in buffer)
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                System.out.println("Waiting for response...");
                socket.receive(request); 

                // unmarshal the request
                ClientInput clientInput = Marshaller.unmarshalClientInput(buffer);
                
                // hand over to controller for processing
                Controller.processRequest(clientInput);
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
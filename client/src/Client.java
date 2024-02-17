package src;

/**
Name: Lee Juin
Group: A52
IP Address: 172.21.144.254
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    static int port = 2222;   // port for QOTD protocol
    static String SERVER = "localhost";    // lab server address
    static String payload = "LeeJuin, A52, ";   // content to be sent over to the server

    public static void main(String[] args) {
        // initialize the socket, binding of socket to server will be done later
        DatagramSocket socket = null;
        try {
            // get the address of the server
            InetAddress serverAddr = InetAddress.getByName(SERVER);
            socket = new DatagramSocket();
            socket.connect(serverAddr, port);   // binds the socket to the server at port 17
            System.out.println("UDP client connected on port " + port + " to server " + serverAddr.getCanonicalHostName());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            // pre-process the content to be sent over by including the client IP address
            String content = payload + InetAddress.getLocalHost().getHostAddress();
            byte[] buffer = content.getBytes("UTF-8");  // convert into bytes
            System.out.println("Content to send: " + content);

            // creates a DatagramPacket that encapsulates the message to be sent (found in buffer)
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            System.out.println("Sending request...");
            socket.send(request);   // sends the message via the channel established
            System.out.println("Request sent to server");

            // a buffer that will store the reply gotten from the server
            byte[] replyBuffer = new byte[512];
            // creates a DatagramPacket that encapsulates the message to be received (to be stored in replyBuffer)
            DatagramPacket reply = new DatagramPacket(replyBuffer, replyBuffer.length);
            System.out.println("Waiting for reply");
            socket.receive(reply);  // receives the message sent by the server

            String replyContent = new String(replyBuffer);  // pre-process the message into String
            System.out.println("Received reply: " + replyContent);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            socket.close();
        }
    }
}

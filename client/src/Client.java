package src;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class Client {
    static int port = 2222;   // port for QOTD protocol
    static String SERVER = "localhost";    // lab server address
    static String payload = "1050";   // content to be sent over to the server

    public static byte[] marshal(int x){
        return new byte[]{
          (byte)(x >> 24),
          (byte)(x >> 16),
          (byte)(x >> 8),
          (byte)(x >> 0)
        };
    }

    public static byte[] joinByteArray(byte[] a, int b) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(a);
            outputStream.write(marshal(b));
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();    
            return null;
        }
    }

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
            byte[] buffer = marshal(1);
            buffer = joinByteArray(buffer, 33);
            buffer = joinByteArray(buffer, 50);
            System.out.println("Content to send: " + buffer);

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

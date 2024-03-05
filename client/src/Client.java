package src;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    static int port = 2222;
    static String SERVER = "localhost"; 

    public static byte[] marshal(int x) {
        return new byte[] {
                (byte) (x >> 24),
                (byte) (x >> 16),
                (byte) (x >> 8),
                (byte) (x >> 0)
        };
    }

    public static byte[] marshal(String x) {
        return x.getBytes();
    }

    public static int unmarshalInt(byte[] x, int startIndex) {
        return ((x[startIndex] & 0xFF) << 24) | ((x[startIndex + 1] & 0xFF) << 16) | ((x[startIndex + 2] & 0xFF) << 8)
                | (x[startIndex + 3] & 0xFF);
    }

    public static String unmarshalString(byte[] x, int startIndex, int length) {
        return new String(x, startIndex, length);
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

    public static byte[] joinByteArray(byte[] a, String b) {
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
            socket.connect(serverAddr, port); // binds the socket to the server at port 17
            System.out.println(
                    "UDP client connected on port " + port + " to server " + serverAddr.getCanonicalHostName());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            int requestID = 0;
            int serviceID = 2;
            String path = "/Users/leejuin/Documents/GitHub/sc4051-distributed-systems/server/src/data.txt";
            int offset = 9000;
            String contentToInsert = "Hello World! This should be inserted into the file";
            int contentByteLength = marshal(contentToInsert).length;

            byte[] data = marshal(requestID);
            data = joinByteArray(data, serviceID);
            data = joinByteArray(data, path.length());
            data = joinByteArray(data, path);
            data = joinByteArray(data, offset);
            data = joinByteArray(data, contentByteLength);
            data = joinByteArray(data, contentToInsert);
            System.out.println("Content to send: " + data);

            // Send request
            DatagramPacket request = new DatagramPacket(data, data.length);
            System.out.println("Sending data");
            socket.send(request);

            // wait for data packet from server
            byte[] serverDataBuffer = new byte[65536];
            DatagramPacket serverData = new DatagramPacket(serverDataBuffer, serverDataBuffer.length);
            socket.receive(serverData);
            System.out.println("Data received from server");
            int status = unmarshalInt(serverDataBuffer, 4);
            int contentLength = unmarshalInt(serverDataBuffer, 8);
            String content = unmarshalString(serverDataBuffer, 12, contentLength);
            System.out.println("Status: " + status);
            System.out.println("Content received: " + content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
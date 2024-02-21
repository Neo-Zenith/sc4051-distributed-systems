package src;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    static int port = 2222;
    static String SERVER = "localhost"; // lab server address
    static String payload = "1050"; // content to be sent over to the server

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
            int serviceID = 1;
            String path = "/Users/leejuin/Documents/GitHub/sc4051-distributed-system/server/src/data.txt";
            int offset = 0;
            int numBytes = 10;

            byte[] data = marshal(requestID);
            data = joinByteArray(data, serviceID);
            data = joinByteArray(data, path.length());
            data = joinByteArray(data, path);
            data = joinByteArray(data, offset);
            data = joinByteArray(data, numBytes);
            System.out.println("Content to send: " + data);

            byte[] header = marshal(requestID);
            header = joinByteArray(header, 0);
            header = joinByteArray(header, data.length);

            // Send header
            DatagramPacket request = new DatagramPacket(header, header.length);
            System.out.println("Sending header...");
            socket.send(request);
            System.out.println("Header sent to server");

            // store ACK for header packet
            byte[] ackBuffer = new byte[4];
            DatagramPacket ack = new DatagramPacket(ackBuffer, ackBuffer.length);
            socket.receive(ack);

            if (unmarshalInt(ackBuffer, 0) == requestID) {
                System.out.println("Received ACK");
            } else {
                System.out.println("Received wrong ACK");
                return;
            }

            // Send data
            request = new DatagramPacket(data, data.length);
            System.out.println("Sending data");
            socket.send(request);

            // wait for header packet from server
            byte[] serverHeaderBuffer = new byte[8];
            DatagramPacket serverHeader = new DatagramPacket(serverHeaderBuffer, 8);
            socket.receive(serverHeader);
            System.out.println("Data header received from server");
            int responseID = unmarshalInt(serverHeaderBuffer, 0);
            int responseLength = unmarshalInt(serverHeaderBuffer, 4);
            System.out.println("Response ID: " + responseID);
            System.out.println("Response Length: " + responseLength);

            // send ACK for header packet
            ackBuffer = marshal(responseID);
            DatagramPacket serverAck = new DatagramPacket(ackBuffer, ackBuffer.length, serverHeader.getAddress(),
                    serverHeader.getPort());
            socket.send(serverAck);
            System.out.println("ACK sent");

            // wait for data packet from server
            byte[] serverDataBuffer = new byte[responseLength];
            DatagramPacket serverData = new DatagramPacket(serverDataBuffer, responseLength);
            socket.receive(serverData);
            System.out.println("Data received from server");
            int status = unmarshalInt(serverDataBuffer, 4);
            String content = unmarshalString(serverDataBuffer, 8, responseLength - 8);
            System.out.println("Status: " + status);
            System.out.println("Content received: " + content);

            // send ACK for header packet
            socket.send(serverAck);
            System.out.println("ACK sent");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
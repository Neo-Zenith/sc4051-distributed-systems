package src;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;
import java.util.Calendar;

public class Client2 {
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

    // Marshal Date into 3 bytes, first 15 bits is year, next 4 bits is month, last 5 bits is day
    public static byte[] marshal(int year, int month, int day) {
        return new byte[] {
                (byte) (year >> 7),
                (byte) (year << 1 | month >> 3),
                (byte) (month << 5 | day)
        };
    }

    public static int unmarshalInt(byte[] x, int startIndex) {
        return ((x[startIndex] & 0xFF) << 24) | ((x[startIndex + 1] & 0xFF) << 16) | ((x[startIndex + 2] & 0xFF) << 8)
                | (x[startIndex + 3] & 0xFF);
    }

    public static String unmarshalString(byte[] x, int startIndex, int length) {
        return new String(x, startIndex, length);
    }

    public static long unmarshalLong(byte[] x, int startIndex) {
        return ((long) (x[startIndex] & 0xFF) << 56) | ((long) (x[startIndex + 1] & 0xFF) << 48)
                | ((long) (x[startIndex + 2] & 0xFF) << 40) | ((long) (x[startIndex + 3] & 0xFF) << 32)
                | ((long) (x[startIndex + 4] & 0xFF) << 24) | ((long) (x[startIndex + 5] & 0xFF) << 16)
                | ((long) (x[startIndex + 6] & 0xFF) << 8) | ((long) (x[startIndex + 7] & 0xFF) << 0);
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

    public static byte[] joinByteArray(byte[] a, Date b) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(a);
            Calendar c = Calendar.getInstance();
            c.setTime(b);
            outputStream.write(marshal(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)));
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
            int offset = 0;
            String contentToInsert = "INSERT";
            int contentByteLength = marshal(contentToInsert).length;

            byte[] data = marshal(requestID);
            System.out.println(serviceID);
            data = joinByteArray(data, serviceID);
            data = joinByteArray(data, path.length());
            data = joinByteArray(data, path);
            // Calendar c = Calendar.getInstance();
            // c.set(2024, 10, 1);
            // data = joinByteArray(data, c.getTime());
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
            //long fileSize = unmarshalLong(serverDataBuffer, 8);
            int contentLength = unmarshalInt(serverDataBuffer, 8);
            System.out.println("Content length: " + contentLength);
            String content = unmarshalString(serverDataBuffer, 12, contentLength);
            //System.out.println("Filesize: " + fileSize);
            System.out.println("Status: " + status);
            System.out.println("Content received: " + content);

            request = new DatagramPacket(data, data.length);
            System.out.println("Sending data");
            socket.send(request);

            byte[] buffer = new byte[65536];
            DatagramPacket request2 = new DatagramPacket(buffer, buffer.length);
            // timeout after 5 seconds
            socket.setSoTimeout(5000);
            try {
                socket.receive(request2);
            } catch(IOException e) {
                System.out.println("Timeout, resending request");
            }

            socket.send(request);
            socket.receive(request2);
            System.out.println("Data received from server");
            int status2 = unmarshalInt(buffer, 4);
            contentLength = unmarshalInt(buffer, 8);
            content = unmarshalString(buffer, 12, contentLength);
            System.out.println("Status: " + status2);
            System.out.println("Content received: " + content);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
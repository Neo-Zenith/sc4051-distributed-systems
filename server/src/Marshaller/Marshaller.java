package src.marshaller;

import java.util.Calendar;
import java.util.Date;

/**
 * Class to marshal and unmarshal the client's request <br>
 * This includes the request ID, service ID, file path and payload <br>
 * Marshalling is done by type
 * 
 * @author Lee Juin
 * @version 1.0
 */
public class Marshaller {
    /**
     * Unmarshal the input into a ClientPacket object
     * 
     * @param input The byte array containing the request
     * @return The ClientPacket object containing the request details
     */
    public static ClientPacket unmarshalClientPacket(byte[] input) {
        int requestID = Marshaller.unmarshalRequestID(input);
        int serviceID = Marshaller.unmarshalServiceID(input);

        switch (serviceID) {
            case 1:
                return unmarshalService1(requestID, input);
            case 2:
                return unmarshalService2(requestID, input);
            case 3:
                return unmarshalService3(requestID, input);
            case 4:
                return unmarshalService4(requestID, input);
            case 5:
                return unmarshalService5(requestID, input);
            default:
                return null;
        }
    }

    /**
     * Obtain the request ID by left-shifting the first 4 bytes of the input
     * 
     * @param input byte array containing the request payload
     * @return request ID
     */
    public static int unmarshalRequestID(byte[] input) {
        // Convert first 4 bytes into request ID
        return Marshaller.unmarshalInt(input, 0);
    }

    /**
     * Obtain the service requested by left-shifting the
     * next 4 bytes of the input
     * 
     * @param input byte array containing the request payload
     * @return service number
     */
    public static int unmarshalServiceID(byte[] input) {
        // Convert next 4 bytes into service number
        return Marshaller.unmarshalInt(input, 4);
    }

    /**
     * Unmarshal the input for Service 1<br>
     * Format:<br>
     * - 4 bytes for request ID<br>
     * - 4 bytes for service ID<br>
     * - 4 bytes for length of file path<br>
     * - variable length for file path<br>
     * - 4 bytes for offset<br>
     * - 4 bytes for number of bytes to read<br>
     * 
     * @param requestID request ID
     * @param input     byte array containing the request
     * @return ClientPacket object containing the request details for Service 1
     */
    public static ClientPacket unmarshalService1(int requestID, byte[] input) {
        // Convert the next 4 bytes into the length of the file path
        int filePathLength = unmarshalInt(input, 8);

        // Convert the next filePathLength bytes into the file path
        String filePath = unmarshalString(input, 12, filePathLength);

        // Convert the next 4 bytes into the offset
        int offset = unmarshalInt(input, 12 + filePathLength);

        // Convert the next 4 bytes into the number of bytes to read
        int numBytes = unmarshalInt(input, 16 + filePathLength);

        ClientPayload clientPayload = new ClientPayload(offset, numBytes);
        return new ClientPacket(requestID, 1, filePath, clientPayload);
    }

    /**
     * Unmarshal the input for Service 2<br>
     * Format:<br>
     * - 4 bytes for request ID<br>
     * - 4 bytes for service ID<br>
     * - 4 bytes for length of file path<br>
     * - variable length for file path<br>
     * - 4 bytes for offset<br>
     * - 4 bytes for length of bytes to insert<br>
     * - variable length for bytes to insert<br>
     * 
     * @param requestID request ID
     * @param input     byte array containing the request
     * @return ClientPacket object containing the request details for Service 2
     */
    public static ClientPacket unmarshalService2(int requestID, byte[] input) {
        // Convert the next 4 bytes into the length of the file path
        int filePathLength = unmarshalInt(input, 8);

        // Convert the next filePathLength bytes into the file path
        String filePath = unmarshalString(input, 12, filePathLength);

        // Convert the next 4 bytes into the offset
        int offset = unmarshalInt(input, 12 + filePathLength);

        // Convert the next 4 bytes into the length of the bytes to insert
        int bytesToInsertLength = unmarshalInt(input, 16 + filePathLength);

        // Convert the next bytesToInsertLength bytes into the bytes to insert
        byte[] bytesToInsert = new byte[bytesToInsertLength];
        for (int i = 20 + filePathLength; i < 20 + filePathLength + bytesToInsertLength; i++) {
            bytesToInsert[i - (20 + filePathLength)] = input[i];
        }

        ClientPayload clientPayload = new ClientPayload(offset, bytesToInsert);
        return new ClientPacket(requestID, 2, filePath, clientPayload);
    }

    /**
     * Unmarshal the input for Service 3<br>
     * Format:<br>
     * - 4 bytes for request ID<br>
     * - 4 bytes for service ID<br>
     * - 4 bytes for length of file path<br>
     * - variable length for file path<br>
     * - 8 bytes for expiry date in long (time since epoch in milliseconds)<br>
     * 
     * @param requestID The request ID
     * @param input     The byte array containing the request
     * @return ClientPacket object containing the request details for Service 3
     */
    public static ClientPacket unmarshalService3(int requestID, byte[] input) {
        // Convert the next 4 bytes into the length of the file path
        int filePathLength = unmarshalInt(input, 8);

        // Convert the next filePathLength bytes into the file path
        String filePath = unmarshalString(input, 12, filePathLength);

        // Convert next 8 bytes into expiryDate in long
        long expiryDateInMillis = unmarshalLong(input, 12 + filePathLength);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(expiryDateInMillis);
        Date expiryDate = calendar.getTime();

        System.out.println("Expiry Date: " + expiryDate);

        return new ClientPacket(requestID, 3, filePath, new ClientPayload(expiryDate));
    }

    /**
     * Unmarshal the input for Service 4<br>
     * Format:<br>
     * - 4 bytes for request ID<br>
     * - 4 bytes for service ID<br>
     * - 4 bytes for length of file path<br>
     * - variable length for file path<br>
     * 
     * @param requestID The request ID
     * @param input     The byte array containing the request
     * @return ClientPacket object containing the request details for Service 4
     */
    public static ClientPacket unmarshalService4(int requestID, byte[] input) {
        // Convert the next 4 bytes into the length of the file path
        int filePathLength = unmarshalInt(input, 8);

        // Convert the next filePathLength bytes into the file path
        String filePath = unmarshalString(input, 12, filePathLength);

        return new ClientPacket(requestID, 4, filePath, new ClientPayload());
    }

    /**
     * Unmarshal the input for Service 5<br>
     * Format:<br>
     * - 4 bytes for request ID<br>
     * - 4 bytes for service ID<br>
     * - 4 bytes for length of file path<br>
     * - variable length for file path<br>
     * - 4 bytes for offset<br>
     * - 4 bytes for length of bytes to delete<br>
     * 
     * @param requestID The request ID
     * @param input     The byte array containing the request
     * @return ClientPacket object containing the request details for Service 5
     */
    public static ClientPacket unmarshalService5(int requestID, byte[] input) {
        // Convert the next 4 bytes into the length of the file path
        int filePathLength = unmarshalInt(input, 8);

        // Convert the next filePathLength bytes into the file path
        String filePath = unmarshalString(input, 12, filePathLength);

        // Convert the next 4 bytes into the offset
        int offset = unmarshalInt(input, 12 + filePathLength);

        // Convert the next 4 bytes into the length of the bytes to delete
        int numBytes = unmarshalInt(input, 16 + filePathLength);

        ClientPayload clientPayload = new ClientPayload(offset, numBytes);
        return new ClientPacket(requestID, 5, filePath, clientPayload);
    }

    /**
     * Unmarshal 4 bytes into an integer
     * 
     * @param b          The byte array to unmarshal
     * @param startIndex The start index of the byte array to read from
     * @return The integer value
     */
    public static int unmarshalInt(byte[] b, int startIndex) {
        return ((b[startIndex] & 0xFF) << 24) | ((b[startIndex + 1] & 0xFF) << 16) | ((b[startIndex + 2] & 0xFF) << 8)
                | (b[startIndex + 3] & 0xFF);
    }

    /**
     * Unmarshal the byte array into a string
     * 
     * @param b          The byte array to unmarshal
     * @param startIndex The start index of the byte array to read from
     * @param length     The length of the string
     * @return The string
     */
    public static String unmarshalString(byte[] b, int startIndex, int length) {
        char[] c = new char[length];
        for (int i = startIndex; i < startIndex + length; i++) {
            c[i - startIndex] = (char) (b[i]);
        }
        return new String(c);
    }

    /**
     * Unmarshal 8 bytes into a long
     * 
     * @param b          The byte array to unmarshal
     * @param startIndex The start index of the byte array to read from
     * @return The long value
     */
    public static long unmarshalLong(byte[] b, int startIndex) {
        return ((long) (b[startIndex] & 0xFF) << 56) | ((long) (b[startIndex + 1] & 0xFF) << 48)
                | ((long) (b[startIndex + 2] & 0xFF) << 40) | ((long) (b[startIndex + 3] & 0xFF) << 32)
                | ((long) (b[startIndex + 4] & 0xFF) << 24) | ((long) (b[startIndex + 5] & 0xFF) << 16)
                | ((long) (b[startIndex + 6] & 0xFF) << 8) | ((long) (b[startIndex + 7] & 0xFF) << 0);
    }

    /**
     * Marshal an integer into a byte array
     * 
     * @param x The integer to marshal
     * @return The byte array containing the marshalled integer
     */
    public static byte[] marshal(int x) {
        return new byte[] { (byte) (x >> 24), (byte) (x >> 16), (byte) (x >> 8), (byte) (x >> 0) };
    }

    /**
     * Marshal a string into a byte array
     * 
     * @param s The string to marshal
     * @return The byte array containing the marshalled string
     */
    public static byte[] marshal(String s) {
        byte[] result = new byte[s.length()];
        for (int i = 0; i < s.length(); i++) {
            result[i] = (byte) s.charAt(i);
        }
        return result;
    }

    /**
     * Marshal a long into a byte array
     * 
     * @param x The long to marshal
     * @return The byte array containing the marshalled long
     */
    public static byte[] marshal(long x) {
        return new byte[] { (byte) (x >> 56), (byte) (x >> 48), (byte) (x >> 40), (byte) (x >> 32), (byte) (x >> 24),
                (byte) (x >> 16), (byte) (x >> 8), (byte) (x >> 0) };
    }

    /**
     * Append an integer to a byte array
     * 
     * @param byteArray The byte array to append to
     * @param x         The integer to append
     * @return The new byte array containing the appended integer
     */
    public static byte[] appendInt(byte[] byteArray, int x) {
        byte[] intBytes = Marshaller.marshal(x);
        byte[] newByteArray = new byte[byteArray.length + intBytes.length];
        System.arraycopy(byteArray, 0, newByteArray, 0, byteArray.length);
        System.arraycopy(intBytes, 0, newByteArray, byteArray.length, intBytes.length);
        return newByteArray;
    }

    /**
     * Append a string to a byte array
     * 
     * @param byteArray The byte array to append to
     * @param s         The string to append
     * @return The new byte array containing the appended string
     */
    public static byte[] appendString(byte[] byteArray, String s) {
        byte[] stringBytes = Marshaller.marshal(s);
        byte[] newByteArray = new byte[byteArray.length + stringBytes.length];
        System.arraycopy(byteArray, 0, newByteArray, 0, byteArray.length);
        System.arraycopy(stringBytes, 0, newByteArray, byteArray.length, stringBytes.length);
        return newByteArray;
    }

    /**
     * Append a long to a byte array
     * 
     * @param byteArray The byte array to append to
     * @param x         The long to append
     * @return The new byte array containing the appended long
     */
    public static byte[] appendLong(byte[] byteArray, long x) {
        byte[] longBytes = Marshaller.marshal(x);
        byte[] newByteArray = new byte[byteArray.length + longBytes.length];
        System.arraycopy(byteArray, 0, newByteArray, 0, byteArray.length);
        System.arraycopy(longBytes, 0, newByteArray, byteArray.length, longBytes.length);
        return newByteArray;
    }
}

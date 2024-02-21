package src.Marshaller;

public class Marshaller {
    public static ClientPacket unmarshalClientPacket(byte[] input) {
        int requestID = Marshaller.unmarshalRequestID(input);
        int serviceID = Marshaller.unmarshalServiceID(input);

        switch (serviceID) {
            case 1:
                return unmarshalService1(requestID, input);
            case 2:
                return unmarshalService2(requestID, input);
            default:
                int requestLength = unmarshalRequestLength(input);
                System.out.println(requestLength);
                return new ClientPacket(requestID, requestLength);
        }
    }

    /**
     * Obtain the request ID by left-shifting the first 4 bytes of the input
     * 
     * @param input
     * @return
     */
    public static int unmarshalRequestID(byte[] input) {
        // Convert first 4 bytes into request ID
        return Marshaller.unmarshalInt(input, 0);
    }

    /**
     * Obtain the service requested by left-shifting the
     * next 4 bytes of the input
     * 
     * @param input byte array containing the request
     * @return service number
     */
    public static int unmarshalServiceID(byte[] input) {
        // Convert first 4 bytes into service number
        return Marshaller.unmarshalInt(input, 4);
    }

    /**
     * Obtain the length of the request by left-shifting
     * the next 4 bytes of the input
     * This length is for next packet
     * 
     * @param input
     * @return
     */
    public static int unmarshalRequestLength(byte[] input) {
        // Convert the next 4 bytes into the length of the request
        return Marshaller.unmarshalInt(input, 8);
    }

    /**
     * Unmarshal the input for Service 1
     * Service 1 will have a file path, offset and number of bytes to read
     * The corresponding fields are string, int and int respectively
     * String is variable length, the rest are fixed length
     * 
     * @param requestID request ID
     * @param input     byte array containing the request
     * @return InputFormat object containing the request details for Service 1
     */
    public static ClientPacket unmarshalService1(int requestID, byte[] input) {
        // Convert the next 4 bytes into the length of the file path
        int filePathLength = 0;
        for (int i = 8; i < 12; i++) {
            filePathLength = (filePathLength << 8) | (input[i] & 0xff);
        }

        // Convert the next filePathLength bytes into the file path
        String filePath = new String(input, 12, filePathLength);

        // Convert the next 4 bytes into the offset
        int offset = 0;
        for (int i = 12 + filePathLength; i < 16 + filePathLength; i++) {
            offset = (offset << 8) | (input[i] & 0xff);
        }

        // Convert the next 4 bytes into the number of bytes to read
        int numBytes = 0;
        for (int i = 16 + filePathLength; i < 20 + filePathLength; i++) {
            numBytes = (numBytes << 8) | (input[i] & 0xff);
        }

        ClientPayload clientPayload = new ClientPayload(offset, numBytes);
        return new ClientPacket(requestID, 1, filePath, clientPayload);
    }

    /**
     * Unmarshal the input for Service 2
     * Service 2 will have a file path, offset and bytes to insert
     * The corresponding fields are string, int and byte array respectively
     * String is variable length, the rest are fixed length
     * 
     * @param requestID request ID
     * @param input     byte array containing the request
     * @return InputFormat object containing the request details for Service 2
     */
    public static ClientPacket unmarshalService2(int requestID, byte[] input) {
        // Convert the next 4 bytes into the length of the file path
        int filePathLength = 0;
        for (int i = 8; i < 12; i++) {
            filePathLength = (filePathLength << 8) | (input[i] & 0xff);
        }

        // Convert the next filePathLength bytes into the file path
        String filePath = new String(input, 12, filePathLength);

        // Convert the next 4 bytes into the offset
        int offset = 0;
        for (int i = 12 + filePathLength; i < 16 + filePathLength; i++) {
            offset = (offset << 8) | (input[i] & 0xff);
        }

        // Convert the next 4 bytes into the length of the bytes to insert
        int bytesToInsertLength = 0;
        for (int i = 16 + filePathLength; i < 20 + filePathLength; i++) {
            bytesToInsertLength = (bytesToInsertLength << 8) | (input[i] & 0xff);
        }

        // Convert the next bytesToInsertLength bytes into the bytes to insert
        byte[] bytesToInsert = new byte[bytesToInsertLength];
        for (int i = 20 + filePathLength; i < 20 + filePathLength + bytesToInsertLength; i++) {
            bytesToInsert[i - (20 + filePathLength)] = input[i];
        }

        ClientPayload clientPayload = new ClientPayload(offset, bytesToInsert);
        return new ClientPacket(requestID, 2, filePath, clientPayload);
    }

    public static int unmarshalInt(byte[] b, int startIndex) {
        return ((b[startIndex] & 0xFF) << 24) | ((b[startIndex + 1] & 0xFF) << 16) | ((b[startIndex + 2] & 0xFF) << 8)
                | (b[startIndex + 3] & 0xFF);
    }

    public static String unmarshalString(byte[] b, int startIndex, int length) {
        char[] c = new char[length];
        for (int i = startIndex; i < startIndex + length; i++) {
            c[i - startIndex] = (char) (b[i]);
        }
        return new String(c);
    }

    // right-shift the integer by 24, 16, 8 and 0 bits respectively
    public static byte[] marshal(int x) {
        return new byte[] { (byte) (x >> 24), (byte) (x >> 16), (byte) (x >> 8), (byte) (x >> 0) };
    }

    public static byte[] marshal(String s) {
        byte[] result = new byte[s.length()];
        for (int i = 0; i < s.length(); i++) {
            result[i] = (byte) s.charAt(i);
        }
        return result;
    }

    public static byte[] appendInt(byte[] byteArray, int x) {
        byte[] intBytes = Marshaller.marshal(x);
        byte[] newByteArray = new byte[byteArray.length + intBytes.length];
        System.arraycopy(byteArray, 0, newByteArray, 0, byteArray.length);
        System.arraycopy(intBytes, 0, newByteArray, byteArray.length, intBytes.length);
        return newByteArray;
    }

    public static byte[] appendString(byte[] byteArray, String s) {
        byte[] stringBytes = Marshaller.marshal(s);
        byte[] newByteArray = new byte[byteArray.length + stringBytes.length];
        System.arraycopy(byteArray, 0, newByteArray, 0, byteArray.length);
        System.arraycopy(stringBytes, 0, newByteArray, byteArray.length, stringBytes.length);
        return newByteArray;

    }
}

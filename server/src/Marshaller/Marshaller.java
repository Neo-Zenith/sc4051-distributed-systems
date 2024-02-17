package src.Marshaller;

public class Marshaller {
    public static ClientInput unmarshalClientInput(byte[] input) {
        int requestID = Marshaller.unmarshalRequestID(input);
        int serviceID = Marshaller.unmarshalServiceID(input);

        switch (serviceID) {
            case 1:
                return unmarshalService1(requestID, input);
            case 2:
                return unmarshalService2(requestID, input);
            default:
                return null;
        }
    }
    
    /**
     * Obtain the request ID by left-shifting the first 4 bytes of the input
     * @param input
     * @return
     */
    public static int unmarshalRequestID(byte[] input) {
        // Convert first 4 bytes into request ID
        int requestID = 0;
        for (int i = 0; i < 4; i++) {
            requestID = (requestID << 8) | (input[i] & 0xff);
        }
        return requestID;
    }

    /**
     * Obtain the service requested by left-shifting the 
     * next 4 bytes of the input
     * @param input     byte array containing the request
     * @return          service number
     */
    public static int unmarshalServiceID(byte[] input) {
        // Convert first 4 bytes into service number
        int serviceNumber = 0;
        for (int i = 4; i < 8; i++) {
            serviceNumber = (serviceNumber << 8) | (input[i] & 0xff);
        }
        return serviceNumber;
    }

    /**
     * Unmarshal the input for Service 1
     * Service 1 will have a file path, offset and number of bytes to read
     * The corresponding fields are string, int and int respectively
     * String is variable length, the rest are fixed length
     * @param requestID    request ID
     * @param input    byte array containing the request
     * @return          InputFormat object containing the request details for Service 1
     */
    public static ClientInput unmarshalService1(int requestID, byte[] input) {
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

        return new ClientInput(requestID, 1, filePath, offset, numBytes);
    }

    /**
     * Unmarshal the input for Service 2
     * Service 2 will have a file path, offset and bytes to insert
     * The corresponding fields are string, int and byte array respectively
     * String is variable length, the rest are fixed length
     * @param requestID    request ID
     * @param input    byte array containing the request
     * @return          InputFormat object containing the request details for Service 2
     */
    public static ClientInput unmarshalService2(int requestID, byte[] input) {
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

        return new ClientInput(requestID, 2, filePath, offset, bytesToInsert);
    }
}

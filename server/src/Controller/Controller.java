package src.Controller;

import java.net.*;

import src.Server;
import src.Comms.ClientDetails;
import src.Marshaller.ClientPacket;
import src.Marshaller.Marshaller;
import src.Services.Service1;
import src.Services.Service2;
import src.Services.Service4;
import src.Services.Service5;

public class Controller {
    /**
     * Process the request from the client
     * 
     * @param request     The request packet
     * @param clientInput The unmarshalled client packet
     */
    public static void processRequest(DatagramPacket request, ClientPacket clientInput) {
        int serviceID = clientInput.getServiceID();
        String filePath;
        int offset;
        int numBytes;

        switch (serviceID) {
            case 1:
                filePath = clientInput.getFilePath();
                offset = clientInput.getClientPayload().getOffset();
                numBytes = clientInput.getClientPayload().getNumBytes();
                System.out.println("------------------ INFO ---------------------");
                System.out.println("Service: Read from file");
                System.out.println("File path: " + filePath);
                System.out.println("Offset: " + offset);
                System.out.println("Number of bytes: " + numBytes);
                Service1 service1 = new Service1(filePath, offset, numBytes);
                String content = service1.readFromFile();
                System.out.println("Content: " + content);
                if (content == null) {
                    content = "Error reading file. File not found.";
                    Controller.sendService1Response(request, 0, content);
                } else {
                    Controller.sendService1Response(request, 1, content);
                }
                break;
            case 2:
                filePath = clientInput.getFilePath();
                offset = clientInput.getClientPayload().getOffset();
                byte[] bytesToInsert = clientInput.getClientPayload().getBytesToInsert();
                System.out.println("------------------ INFO ---------------------");
                System.out.println("Service: Write to file");
                System.out.println("File path: " + filePath);
                System.out.println("Offset: " + offset);
                System.out.println(
                        "Bytes to insert: " + Marshaller.unmarshalString(bytesToInsert, 0, bytesToInsert.length));
                Service2 service2 = new Service2(filePath, offset, bytesToInsert);
                int code = service2.writeToFile();
                String message = "";
                switch (code) {
                    case 200:
                        message += "Bytes written to file. ";
                        message += "Written bytes: "
                                + Marshaller.unmarshalString(bytesToInsert, 0, bytesToInsert.length);
                        Controller.sendService2Response(request, 1, message);
                        break;
                    case 400:
                        message += "Error writing to file. Offset exceeded file length.";
                        Controller.sendService2Response(request, 0, message);
                        break;
                    case 404:
                        message += "Error writing to file. File not found.";
                        Controller.sendService2Response(request, 0, message);
                        break;
                }
                break;
            case 4:
                filePath = clientInput.getFilePath();
                System.out.println("------------------ INFO ---------------------");
                System.out.println("Service: Get file length in bytes");
                System.out.println("File path: " + filePath);
                Service4 service4 = new Service4(filePath);
                long fileSize = service4.getFileSize();
                message = "";
                if (fileSize == -1) {
                    message += "Error reading file. File not found.";
                    Controller.sendService4Response(request, 0, fileSize, message);
                } else {
                    message += "File size retrieved in bytes.";
                    Controller.sendService4Response(request, 1, fileSize, message);
                }
                break;
            case 5:
                filePath = clientInput.getFilePath();
                System.out.println("------------------ INFO ---------------------");
                System.out.println("Service: Delete file");
                System.out.println("File path: " + filePath);
                Service5 service5 = new Service5(filePath);
                boolean deleted = service5.deleteFile();
                message = "";
                if (deleted) {
                    message += "File deleted.";
                    Controller.sendService5Response(request, 1, message);
                } else {
                    message += "Error deleting file. File not found.";
                    Controller.sendService5Response(request, 0, message);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Format:
     * responseID (4 bytes)
     * status (4 bytes)
     * content length (4 bytes)
     * content (variable length)
     * 
     * @param request The request packet
     * @param status  The status of the response (0 = error, 1 = success)
     * @param content The content of the response to be marshalled
     */
    public static void sendService1Response(DatagramPacket request, int status, String content) {
        ClientDetails clientDetails = Server.getClientDetails(request);
        int responseID = Server.getRequests().get(clientDetails);
        System.out.println("-------------- Response packet --------------");
        System.out.println("Response ID: " + responseID);
        System.out.println("Status: " + status);
        // Data packet
        byte[] dataBuffer = Marshaller.marshal(responseID);
        dataBuffer = Marshaller.appendInt(dataBuffer, status);
        int contentLength = content.length();
        System.out.println("Content length: " + contentLength);
        dataBuffer = Marshaller.appendInt(dataBuffer, contentLength);
        System.out.println("Content: " + content);
        dataBuffer = Marshaller.appendString(dataBuffer, content);
        Server.sendReply(request, dataBuffer);
    }

    /**
     * Format:
     * responseID (4 bytes)
     * status (4 bytes)
     * message length (4 bytes)
     * message (variable length)
     * 
     * @param request The request packet
     * @param status  The status of the response (0 = error, 1 = success)
     * @param message The message of the response to be marshalled
     */
    public static void sendService2Response(DatagramPacket request, int status, String message) {
        ClientDetails clientDetails = Server.getClientDetails(request);
        int responseID = Server.getRequests().get(clientDetails);
        System.out.println("-------------- Response packet --------------");
        System.out.println("Response ID: " + responseID);
        System.out.println("Status: " + status);
        // Data packet
        byte[] dataBuffer = Marshaller.marshal(responseID);
        dataBuffer = Marshaller.appendInt(dataBuffer, status);
        int messageLength = message.length();
        System.out.println("Message length: " + messageLength);
        dataBuffer = Marshaller.appendInt(dataBuffer, messageLength);
        System.out.println("Mesage: " + message);
        dataBuffer = Marshaller.appendString(dataBuffer, message);
        Server.sendReply(request, dataBuffer);
    }

    /**
     * Format
     * responseID (4 bytes)
     * status (4 bytes)
     * file size (8 bytes)
     * message length (4 bytes)
     * message (variable length)
     * 
     * @param request  The request packet
     * @param status   The status of the response (0 = error, 1 = success)
     * @param fileSize The file size in bytes
     * @param message  The message of the response to be marshalled
     */
    public static void sendService4Response(DatagramPacket request, int status, long fileSize, String message) {
        ClientDetails clientDetails = Server.getClientDetails(request);
        int responseID = Server.getRequests().get(clientDetails);
        System.out.println("-------------- Response packet --------------");
        System.out.println("Response ID: " + responseID);
        System.out.println("Status: " + status);
        // Data packet
        byte[] dataBuffer = Marshaller.marshal(responseID);
        dataBuffer = Marshaller.appendInt(dataBuffer, status);
        dataBuffer = Marshaller.appendLong(dataBuffer, fileSize);
        System.out.println("File size: " + fileSize + " bytes");
        int messageLength = message.length();
        System.out.println("Message length: " + messageLength);
        dataBuffer = Marshaller.appendInt(dataBuffer, messageLength);
        System.out.println("Mesage: " + message);
        dataBuffer = Marshaller.appendString(dataBuffer, message);
        Server.sendReply(request, dataBuffer);
    }

    /**
     * Format:
     * responseID (4 bytes)
     * status (4 bytes)
     * message length (4 bytes)
     * message (variable length)
     * 
     * @param request The request packet
     * @param status  The status of the response (0 = error, 1 = success)
     * @param message The message of the response to be marshalled
     */
    public static void sendService5Response(DatagramPacket request, int status, String message) {
        ClientDetails clientDetails = Server.getClientDetails(request);
        int responseID = Server.getRequests().get(clientDetails);
        System.out.println("-------------- Response packet --------------");
        System.out.println("Response ID: " + responseID);
        System.out.println("Status: " + status);
        // Data packet
        byte[] dataBuffer = Marshaller.marshal(responseID);
        dataBuffer = Marshaller.appendInt(dataBuffer, status);
        int messageLength = message.length();
        System.out.println("Message length: " + messageLength);
        dataBuffer = Marshaller.appendInt(dataBuffer, messageLength);
        System.out.println("Message: " + message);
        dataBuffer = Marshaller.appendString(dataBuffer, message);
        Server.sendReply(request, dataBuffer);

        System.out.println(Marshaller.unmarshalInt(dataBuffer, 8));
        System.out.println(Marshaller.unmarshalString(dataBuffer, 12, messageLength));
    }
}

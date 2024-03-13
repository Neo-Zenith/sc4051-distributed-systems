package src.controller;

import java.net.*;
import java.util.Date;

import src.Server;
import src.Server.TimeoutFrequency;
import src.comms.ClientDetails;
import src.marshaller.ClientPacket;
import src.marshaller.Marshaller;
import src.services.Service1;
import src.services.Service2;
import src.services.Service3;
import src.services.Service4;
import src.services.Service5;

/**
 * The Controller class is responsible for:
 * - Processing the request from the client for each service <br/>
 * - Invoke the appropriate service <br/>
 * - Marshal service responses with the help of {@link Marshaller} <br/>
 * - Invoke {@link Server} method to send the response back to the client <br/>
 * 
 * @author Lee Juin
 * @version 1.0
 */
public class Controller {
    /**
     * Flag to indicate if at-most-once semantics is used
     */
    private static boolean atMostOnce;
    /**
     * The timeout frequency
     */
    private static TimeoutFrequency timeoutFrequency;
    /**
     * The timeout counter to know when to timeout<br/>
     * Not used when timeout frequency is NEVER or RANDOM
     */
    private static int timeoutCounter = 0;

    /**
     * Process the request from the client
     * 
     * @param request      The request packet
     * @param clientPacket The unmarshalled client packet
     */
    public static void processRequest(DatagramPacket request, ClientPacket clientPacket) {
        // When at-most-once semantics is used, check if the request ID has been
        // processed
        ClientDetails clientDetails = Server.getClientDetails(request);
        int requestID = clientPacket.getRequestID();

        if (atMostOnce) {
            if (Server.getReplyMessages(clientDetails) != null
                    && Server.getReplyMessages(clientDetails).containsKey(requestID)) {
                System.out.println(
                        "Request ID: " + requestID + " has been processed. Retrieve result from store instead.");
                byte[] storedReply = Server.getReplyMessages(clientDetails).get(requestID);
                Server.sendReply(requestID, request, storedReply);
                return;
            }
        }
        int serviceID = clientPacket.getServiceID();
        String filePath;
        int offset;
        int numBytes;

        switch (serviceID) {
            case 1:
                filePath = clientPacket.getFilePath();
                offset = clientPacket.getClientPayload().getOffset();
                numBytes = clientPacket.getClientPayload().getNumBytes();
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
                filePath = clientPacket.getFilePath();
                offset = clientPacket.getClientPayload().getOffset();
                byte[] bytesToInsert = clientPacket.getClientPayload().getBytesToInsert();
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
                        service1 = new Service1(filePath);
                        content = service1.readFullFile();
                        Controller.sendService2Response(request, 1, message);
                        Controller.broadcastUpdate(filePath, content);
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
            case 3:
                filePath = clientPacket.getFilePath();
                Date expiryDate = clientPacket.getClientPayload().getExpiryDate();
                int resposneID = clientPacket.getRequestID();
                System.out.println("------------------ INFO ---------------------");
                System.out.println("Service: Add client to monitor file");
                System.out.println("File path: " + filePath);
                Service3.addRecord(resposneID, request, filePath, expiryDate);
                String message = "Client added to monitor file.";
                Controller.sendService3Response(request, 1, message);
                break;
            case 4:
                filePath = clientPacket.getFilePath();
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
                filePath = clientPacket.getFilePath();
                offset = clientPacket.getClientPayload().getOffset();
                numBytes = clientPacket.getClientPayload().getNumBytes();
                System.out.println("------------------ INFO ---------------------");
                System.out.println("Service: Delete from file");
                System.out.println("File path: " + filePath);
                Service5 service5 = new Service5(filePath, offset, numBytes);
                // boolean deleted = service5.deleteFile();
                // message = "";
                // if (deleted) {
                // message += "File deleted.";
                // Controller.sendService5Response(request, 1, message);
                // } else {
                // message += "Error deleting file. File not found.";
                // Controller.sendService5Response(request, 0, message);
                // }
                int code = service5.deleteFromFile();
                String message = "";
                switch (code) {
                    case 200:
                        message += "Bytes deleted from file. ";
                        service1 = new Service1(filePath);
                        content = service1.readFullFile();
                        message += "Updated file: " + content;
                        Controller.sendService5Response(request, 1, message);
                        Controller.broadcastUpdate(filePath, content);
                        break;
                    case 400:
                        message += "Error deleting from file. Invalid offset and numBytes.";
                        Controller.sendService5Response(request, 0, message);
                        break;
                    case 404:
                        message += "Error deleting from file. File not found.";
                        Controller.sendService5Response(request, 0, message);
                        break;
                }
                break;
            default:
                break;
        }
    }

    /**
     * Marshal the service 1's response before sending to the client <br/>
     * Format:<br/>
     * - responseID (4 bytes)<br/>
     * - status (4 bytes)<br/>
     * - content length (4 bytes)<br/>
     * - content (variable length)<br/>
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
        Server.sendReply(responseID, request, dataBuffer);
    }

    /**
     * Marshal the service 2's response before sending to the client<br/>
     * Format:<br/>
     * - responseID (4 bytes)<br/>
     * - status (4 bytes)<br/>
     * - message length (4 bytes)<br/>
     * - message (variable length)<br/>
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
        Server.sendReply(responseID, request, dataBuffer);
    }

    /**
     * Marshal the service 3's response before sending to the client<br/>
     * Format:<br/>
     * - responseID (4 bytes)<br/>
     * - status (4 bytes)<br/>
     * - message length (4 bytes)<br/>
     * - message (variable length)<br/>
     * 
     * @param request The request packet
     * @param status  The status of the response (0 = error, 1 = success)
     * @param message The message of the response to be marshalled
     */
    public static void sendService3Response(DatagramPacket request, int status, String message) {
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
        Server.sendReply(responseID, request, dataBuffer);
    }

    /**
     * Marshal the service 4's response before sending to the client<br/>
     * Format<br/>
     * - responseID (4 bytes)<br/>
     * - status (4 bytes)<br/>
     * - file size (8 bytes)<br/>
     * - message length (4 bytes)<br/>
     * - message (variable length)<br/>
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
        Server.sendReply(responseID, request, dataBuffer);
    }

    /**
     * Marshal the service 5's response before sending to the client<br/>
     * Format:<br/>
     * - responseID (4 bytes)<br/>
     * - status (4 bytes)<br/>
     * - message length (4 bytes)<br/>
     * - message (variable length)<br/>
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
        Server.sendReply(responseID, request, dataBuffer);
    }

    /**
     * Broadcast the update to all the clients monitoring the file<br/>
     * Format:<br/>
     * - responseID (4 bytes)<br/>
     * - status (4 bytes)<br/>
     * - content length (4 bytes)<br/>
     * - content (variable length)<br/>
     * 
     * @param filePath The file path that has been changed
     * @param content  The content of the file after the update
     */
    public static void broadcastUpdate(String filePath, String content) {
        // Remove all the expired client before broadcasting
        Service3.checkExpiry();

        for (DatagramPacket request : Service3.getClientRecords().keySet()) {
            System.out.println("Request: " + request.getAddress() + ":" + request.getPort());
            Service3.Service3Record record = Service3.getClientRecords().get(request);
            if (record.getFilePath().equals(filePath)) {
                System.out.println("Broadcasting update to client: " + request.getAddress() + ":" + request.getPort());
                int responseID = record.getResponseID();
                int status = 1;
                byte[] dataBuffer = Marshaller.marshal(responseID);
                dataBuffer = Marshaller.appendInt(dataBuffer, status);
                int contentLength = content.length();
                System.out.println("Content length: " + contentLength);
                dataBuffer = Marshaller.appendInt(dataBuffer, contentLength);
                System.out.println("Content: " + content);
                dataBuffer = Marshaller.appendString(dataBuffer, content);
                Server.sendReply(responseID, request, dataBuffer);
            }
        }
    }

    /**
     * Set the at-most-once semantics
     * 
     * @param atMostOnce The at-most-once semantics
     */
    public static void setAtMostOnce(boolean atMostOnce) {
        Controller.atMostOnce = atMostOnce;
    }

    /**
     * Set the timeout frequency
     * 
     * @param timeoutFrequency The timeout frequency
     */
    public static void setTimeoutFrequency(TimeoutFrequency timeoutFrequency) {
        Controller.timeoutFrequency = timeoutFrequency;
    }

    /**
     * Check if the request should timeout
     * 
     * @return true if the request should timeout, false otherwise
     */
    public static boolean shouldTimeout() {
        if (Controller.timeoutFrequency == TimeoutFrequency.NEVER) {
            return false;
        } else if (Controller.timeoutFrequency == TimeoutFrequency.EVERY_2_REQUESTS) {
            Controller.timeoutCounter++;
            if (Controller.timeoutCounter == 2) {
                Controller.timeoutCounter = 0;
                return true;
            }
            return false;
        } else if (Controller.timeoutFrequency == TimeoutFrequency.EVERY_4_REQUESTS) {
            Controller.timeoutCounter++;
            if (Controller.timeoutCounter == 4) {
                Controller.timeoutCounter = 0;
                return true;
            }
            return false;
        } else if (Controller.timeoutFrequency == TimeoutFrequency.RANDOM) {
            int random = (int) (Math.random() * 100);
            if (random < 10) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }
}

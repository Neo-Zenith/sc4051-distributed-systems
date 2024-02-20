package src.Controller;
import java.net.*;

import src.Server;
import src.Marshaller.ClientPacket;
import src.Marshaller.Marshaller;
import src.Services.Service1;
import src.Services.Service2;

public class Controller {
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
                System.out.println("Service: Read from file");
                System.out.println("File path: " + filePath);
                System.out.println("Offset: " + offset);
                System.out.println("Number of bytes: " + numBytes);
                Service1 service1 = new Service1(filePath, offset, numBytes);
                String content = service1.readFromFile();
                System.out.println("Content: " + content);
                if (content == null) {
                    content = "Error reading file";
                    Controller.sendService1Response(request, 0, content);
                } else {
                    Controller.sendService1Response(request, 1, content);
                }
                break;
            case 2:
                filePath = clientInput.getFilePath();
                offset = clientInput.getClientPayload().getOffset();
                byte[] bytesToInsert = clientInput.getClientPayload().getBytesToInsert();
                Service2 service2 = new Service2(filePath, offset, bytesToInsert);
                Controller.sendService2Response();
                break;
            default:
                // Default is to send an acknowledgement
                Controller.sendAcknowledgement(request, clientInput.getRequestID());
                break;
        }
    }

    public static void sendAcknowledgement(DatagramPacket request, int requestID) {
        byte[] replyBuffer = Marshaller.marshal(requestID);
        // send the reply
        Server.sendReply(request, replyBuffer);
    }

    public static void sendService1Response(DatagramPacket request, int status, String content) {
        int responseID = Server.getResponseID();

        // Header packet
        int responseLength = 8 + content.length();
        byte[] headerBuffer = Marshaller.marshal(responseID);
        headerBuffer = Marshaller.appendInt(headerBuffer, responseLength);

        // Data packet
        byte[] dataBuffer = Marshaller.marshal(responseID);
        dataBuffer = Marshaller.appendInt(dataBuffer, status);
        dataBuffer = Marshaller.appendString(dataBuffer, content);

        Server.sendReply(request, headerBuffer, dataBuffer);
    }

    public static void sendService2Response() {

    }
}

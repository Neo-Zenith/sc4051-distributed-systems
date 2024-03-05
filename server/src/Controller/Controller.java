package src.Controller;
import java.net.*;

import src.Server;
import src.Comms.ClientDetails;
import src.Marshaller.ClientPacket;
import src.Marshaller.Marshaller;
import src.Services.Service1;
import src.Services.Service2;

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
                System.out.println("--------------------------------");
                System.out.println("Service: Read from file");
                System.out.println("File path: " + filePath);
                System.out.println("Offset: " + offset);
                System.out.println("Number of bytes: " + numBytes);
                Service1 service1 = new Service1(filePath, offset, numBytes);
                String content = service1.readFromFile();
                System.out.println("Content: " + content);
                System.out.println("--------------------------------");
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
                break;
        }
    }

    /**
     * Marshal the response before sending it to the client
     * 
     * @param request   The request packet
     * @param status    The status of the response (0 = error, 1 = success)
     * @param content   The content of the response to be marshalled
     */
    public static void sendService1Response(DatagramPacket request, int status, String content) {
        ClientDetails clientDetails = Server.getClientDetails(request);
        int responseID = Server.getRequests().get(clientDetails);

        // Data packet
        byte[] dataBuffer = Marshaller.marshal(responseID);
        dataBuffer = Marshaller.appendInt(dataBuffer, status);
        dataBuffer = Marshaller.appendString(dataBuffer, content);

        Server.sendReply(request, dataBuffer);
    }

    public static void sendService2Response() {

    }
}

package src.Controller;

import src.Marshaller.ClientInput;
import src.Services.Service1;
import src.Services.Service2;

public class Controller {
    public static void processRequest(ClientInput clientInput) {
        int serviceID = clientInput.getServiceID();
        String filePath;
        int offset;
        int numBytes;

        switch (serviceID) {
            case 1:
                filePath = clientInput.getFilePath();
                offset = clientInput.getOffset();
                numBytes = clientInput.getNumBytes();
                Service1 service1 = new Service1(filePath, offset, numBytes);
                String content = service1.readFromFile();
                break;
            case 2:
                filePath = clientInput.getFilePath();
                offset = clientInput.getOffset();
                byte[] bytesToInsert = clientInput.getBytesToInsert();
                Service2 service2 = new Service2(filePath, offset, bytesToInsert);
                break;
            default:
                break;
        }
    }
}

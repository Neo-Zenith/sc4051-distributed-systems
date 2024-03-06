package src.Services;

import java.util.HashMap;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.net.*;

public class Service3 {
    public static class Service3Record {
        private int responseID;
        private Date expiryDate;
        private String filePath;

        public Service3Record(int responseID, Date expiryDate, String filePath) {
            this.responseID = responseID;
            this.expiryDate = expiryDate;
            this.filePath = filePath;
        }

        public int getResponseID() {
            return responseID;
        }

        public void setResponseID(int responseID) {
            this.responseID = responseID;
        }

        public Date getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(Date expiryDate) {
            this.expiryDate = expiryDate;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }

    private static HashMap<DatagramPacket, Service3Record> clientRecords = new HashMap<DatagramPacket, Service3Record>();

    public static void addRecord(int responseID, DatagramPacket request, String filePath, Date expiryDate) {
        Service3Record record = new Service3Record(responseID, expiryDate, filePath);
        clientRecords.put(request, record);
    }

    public static Service3Record getRecord(DatagramPacket request) {
        return clientRecords.get(request);
    }

    public static HashMap<DatagramPacket, Service3Record> getClientRecords() {
        return clientRecords;
    }

    /**
     * Check if any records have expired and remove them
     */
    public static void checkExpiry() {
        Iterator<Map.Entry<DatagramPacket, Service3Record>> iterator = clientRecords.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<DatagramPacket, Service3Record> entry = iterator.next();
            Service3Record record = entry.getValue();
            if (record.getExpiryDate().before(new Date())) {
                iterator.remove(); 
            }
        }
    }
}

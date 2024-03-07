package src.services;

import java.util.HashMap;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.net.*;

/**
 * Service 3<br>
 * This service will monitor a file for changes and notify the client when the file has been modified<br>
 * The client will specify the file path and the server will monitor the file for changes<br>
 * When the file has been modified, the server will notify the client<br>
 * @author Lee Juin
 * @version 1.0
 */
public class Service3 {
    /**
     * Class to store the details of a subscriber (client)
     */
    public static class Service3Record {
        /**
         * The response ID of the client that will be sent back to the client when the file has been modified
         */
        private int responseID;
        /**
         * The expiry date of the subscription
         */
        private Date expiryDate;
        /**
         * The path to the file
         */
        private String filePath;

        /**
         * Constructor for Service3Record
         * @param responseID    The response ID of the client
         * @param expiryDate    The expiry date of the subscription
         * @param filePath      The path to the file
         */
        public Service3Record(int responseID, Date expiryDate, String filePath) {
            this.responseID = responseID;
            this.expiryDate = expiryDate;
            this.filePath = filePath;
        }

        /**
         * Getter method for the response ID
         * @return  The response ID
         */
        public int getResponseID() {
            return responseID;
        }

        /**
         * Setter method for the response ID
         * @param responseID    The response ID
         */
        public void setResponseID(int responseID) {
            this.responseID = responseID;
        }

        /**
         * Getter method for the expiry date
         * @return  The expiry date
         */
        public Date getExpiryDate() {
            return expiryDate;
        }

        /**
         * Setter method for the expiry date
         * @param expiryDate    The expiry date
         */
        public void setExpiryDate(Date expiryDate) {
            this.expiryDate = expiryDate;
        }

        /**
         * Getter method for the file path
         * @return  The file path
         */
        public String getFilePath() {
            return filePath;
        }

        /**
         * Setter method for the file path
         * @param filePath  The file path
         */
        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }

    /**
     * HashMap to store the records of the clients who are subscribed to the file changes
     */
    private static HashMap<DatagramPacket, Service3Record> clientRecords = new HashMap<DatagramPacket, Service3Record>();

    /**
     * Subscribe a client to file change
     * @param responseID    The response ID of the client
     * @param request       The request packet from the client
     * @param filePath      The path to the file
     * @param expiryDate    The expiry date of the subscription
     */
    public static void addRecord(int responseID, DatagramPacket request, String filePath, Date expiryDate) {
        Service3Record record = new Service3Record(responseID, expiryDate, filePath);
        clientRecords.put(request, record);
    }

    /**
     * Get the record of the client
     * @param request   The request packet from the client
     * @return  The record of the client
     */
    public static Service3Record getRecord(DatagramPacket request) {
        return clientRecords.get(request);
    }

    /**
     * Get the records of the clients
     * @return  The records of the clients
     */
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

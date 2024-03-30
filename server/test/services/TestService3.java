package test.services;

import org.junit.Test;
import org.junit.Assert;
import java.util.Date;
import java.util.Calendar;
import java.net.*;
import java.io.File;
import java.io.FileWriter;

import src.services.Service3;
import src.services.Service3.Service3Record;

public class TestService3 {
    @Test()
    public void testInitializeService3Record() {
        // Test initialization of Service3
        int expectedResponseID = 1;
        String expectedFilePath = "test/file/path";
        Date expecteedExpireDate = new Date();
        Service3Record service3Record = new Service3Record(expectedResponseID, expecteedExpireDate, expectedFilePath);
        
        int actualResponseID = service3Record.getResponseID();
        String actualFilePath = service3Record.getFilePath();
        Date actualExpireDate = service3Record.getExpiryDate();

        System.out.println("Expected response ID: " + expectedResponseID);
        System.out.println("Actual response ID: " + actualResponseID);
        System.out.println("Expected file path: " + expectedFilePath);
        System.out.println("Actual file path: " + actualFilePath);
        System.out.println("Expected expiry date: " + expecteedExpireDate);
        System.out.println("Actual expiry date: " + actualExpireDate);

        Assert.assertEquals(expectedResponseID, actualResponseID);
        Assert.assertEquals(expectedFilePath, actualFilePath);
        Assert.assertEquals(expecteedExpireDate, actualExpireDate);
    }

    @Test()
    public void testAddService3Record() {
        // Create a dummy file at current directory
        String currentDir = System.getProperty("user.dir") + File.separator + "server" + File.separator + "test" + File.separator + "services";
        String originalContentString = "Hello, this is some text written to the file.";
        File file = new File(currentDir + File.separator + "test.txt");

        try {
            file.getParentFile().mkdirs(); 
            file.createNewFile();
            // Write some content to the file
            FileWriter writer = new FileWriter(file);
            writer.write(originalContentString); 
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
        int expectedResponseID = 1;
        Date expectedExpiryDate = new Date();
        String expectedFilePath = currentDir + File.separator + "test.txt";

        boolean recordAdded = Service3.addRecord(expectedResponseID, request, expectedFilePath, expectedExpiryDate);
        Assert.assertTrue(recordAdded);

        Service3Record service3Record = Service3.getClientRecords().get(request);

        int actualResponseID = service3Record.getResponseID();
        String actualFilePath = service3Record.getFilePath();
        Date actualExpiryDate = service3Record.getExpiryDate();

        System.out.println("Expected response ID: " + expectedResponseID);
        System.out.println("Actual response ID: " + actualResponseID);
        System.out.println("Expected file path: " + expectedFilePath);
        System.out.println("Actual file path: " + actualFilePath);
        System.out.println("Expected expiry date: " + expectedExpiryDate);
        System.out.println("Actual expiry date: " + actualExpiryDate);

        Assert.assertEquals(expectedResponseID, actualResponseID);
        Assert.assertEquals(expectedFilePath, actualFilePath);
        Assert.assertEquals(expectedExpiryDate, actualExpiryDate);

        // Clean up
        file.delete();
    }

    @Test()
    public void testRemoveRecordAfterExpiry() {
        // Create a dummy file at current directory
        String currentDir = System.getProperty("user.dir") + File.separator + "server" + File.separator + "test" + File.separator + "services";
        String originalContentString = "Hello, this is some text written to the file.";
        File file = new File(currentDir + File.separator + "test.txt");

        try {
            file.getParentFile().mkdirs(); 
            file.createNewFile();
            // Write some content to the file
            FileWriter writer = new FileWriter(file);
            writer.write(originalContentString); 
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add a date that will expire by the time checkExpiry() is called
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -1);
        Date expiryDate = calendar.getTime();

        DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
        int expectedResponseID = 1;
        String expectedFilePath = currentDir + File.separator + "test.txt";

        Service3.addRecord(expectedResponseID, request, expectedFilePath, expiryDate);

        Service3.checkExpiry();

        Service3Record service3Record = Service3.getClientRecords().get(request);

        Assert.assertNull(service3Record);
        file.delete();
    }

    @Test()
    public void testNotRemoveRecordBeforeExpiry() {
        // Create a dummy file at current directory
        String currentDir = System.getProperty("user.dir") + File.separator + "server" + File.separator + "test" + File.separator + "services";
        String originalContentString = "Hello, this is some text written to the file.";
        File file = new File(currentDir + File.separator + "test.txt");

        try {
            file.getParentFile().mkdirs(); 
            file.createNewFile();
            // Write some content to the file
            FileWriter writer = new FileWriter(file);
            writer.write(originalContentString); 
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add a date that will not expire by the time checkExpiry() is called
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 1);
        Date expiryDate = calendar.getTime();

        DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
        int expectedResponseID = 1;
        String expectedFilePath = currentDir + File.separator + "test.txt";

        Service3.addRecord(expectedResponseID, request, expectedFilePath, expiryDate);

        Service3.checkExpiry();

        Service3Record service3Record = Service3.getClientRecords().get(request);

        Assert.assertNotNull(service3Record);

        Assert.assertEquals(expectedResponseID, service3Record.getResponseID());
        Assert.assertEquals(expectedFilePath, service3Record.getFilePath());
        Assert.assertEquals(expiryDate, service3Record.getExpiryDate());


        // Clean up
        file.delete();
    }
}

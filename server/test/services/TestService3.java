package test.services;

import org.junit.Test;
import org.junit.Assert;
import java.util.Date;
import java.util.Calendar;
import java.net.*;

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

    public void testAddService3Record() {
        DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
        int expectedResponseID = 1;
        Date expectedExpiryDate = new Date();
        String expectedFilePath = "test/file/path";

        Service3.addRecord(expectedResponseID, request, expectedFilePath, expectedExpiryDate);

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
    }

    public void testRemoveRecordAfterExpiry() {
        // Add a date that will expire by the time checkExpiry() is called
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -1);
        Date expiryDate = calendar.getTime();

        DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
        int expectedResponseID = 1;
        String expectedFilePath = "test/file/path";

        Service3.addRecord(expectedResponseID, request, expectedFilePath, expiryDate);

        Service3.checkExpiry();

        Service3Record service3Record = Service3.getClientRecords().get(request);

        Assert.assertNull(service3Record);
    }
}

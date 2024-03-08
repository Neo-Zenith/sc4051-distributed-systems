package test.services;

import org.junit.Assert;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

import src.services.Service1;

public class TestService1 {
    @Test()
    public void testInitializeService1() {
        // Test initialization of Service1
        String expectedFilePath = "test/file/path";
        Service1 service1 = new Service1(expectedFilePath);
        String actualFilePathString = service1.getFilePath();

        System.out.println("Expected file path: " + expectedFilePath);
        System.out.println("Actual file path: " + actualFilePathString);

        Assert.assertEquals(expectedFilePath, actualFilePathString); 

        // Test initialization of Service1 with offset and numBytes
        int expectedOffset = 10;
        int expectedNumBytes = 100;
        service1 = new Service1(expectedFilePath, expectedOffset, expectedNumBytes);

        int actualOffset = service1.getOffset();
        int actualNumBytes = service1.getNumBytes();

        System.out.println("Expected offset: " + expectedOffset);
        System.out.println("Actual offset: " + actualOffset);
        System.out.println("Expected numBytes: " + expectedNumBytes);
        System.out.println("Actual numBytes: " + actualNumBytes);

        Assert.assertEquals(expectedOffset, actualOffset);
        Assert.assertEquals(expectedNumBytes, actualNumBytes);
    }

    @Test
    public void testReadFullFile() {
        // Content to test read
        String expectedReadContent = "Hello, this is some text written to the file.";
        // Create a dummy file at current directory
        String currentDir = System.getProperty("user.dir") + File.separator + "server" + File.separator + "test" + File.separator + "services";
        File file = new File(currentDir + File.separator + "test.txt");
        try {
            file.getParentFile().mkdirs(); 
            file.createNewFile(); 
            FileWriter writer = new FileWriter(file);
            writer.write(expectedReadContent);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Test reading from the file
        Service1 service1 = new Service1(currentDir + File.separator + "test.txt");
        String actualReadContent = service1.readFullFile();

        System.out.println("Expected read content: " + expectedReadContent);
        System.out.println("Actual read content: " + actualReadContent);

        Assert.assertEquals(expectedReadContent, actualReadContent);

        // Clean up
        file.delete();
    }

    @Test()
    public void testReadPartialFile() {
        // Content to test read
        String expectedReadContent = "Hello, this is some text written to the file.";
        // Create a dummy file at current directory
        String currentDir = System.getProperty("user.dir") + File.separator + "server" + File.separator + "test" + File.separator + "services";
        File file = new File(currentDir + File.separator + "test.txt");
        try {
            file.getParentFile().mkdirs(); 
            file.createNewFile(); 
            FileWriter writer = new FileWriter(file);
            writer.write(expectedReadContent);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Test reading from the file with valid numBytes and offset
        Service1 service1 = new Service1(currentDir + File.separator + "test.txt", 0, 5);
        String actualReadContent = service1.readFromFile();

        System.out.println("Expected read content: " + expectedReadContent.substring(0, 5));
        System.out.println("Actual read content: " + actualReadContent);

        Assert.assertEquals(expectedReadContent.substring(0, 5), actualReadContent);


        // Test reading from the file with offset > file length
        service1 = new Service1(currentDir + File.separator + "test.txt", 100, 5);
        actualReadContent = service1.readFromFile();

        System.out.println("Expected read content to be empty string.");
        System.out.println("Actual read content: " + actualReadContent);

        Assert.assertEquals("", actualReadContent);


        // Test reading from the file with numBytes > file length
        service1 = new Service1(currentDir + File.separator + "test.txt", 0, 100);
        actualReadContent = service1.readFromFile();

        System.out.println("Expected read content: " + expectedReadContent);
        System.out.println("Actual read content: " + actualReadContent);

        Assert.assertEquals(expectedReadContent, actualReadContent);

        // Test reading from the file with offset at last character
        service1 = new Service1(currentDir + File.separator + "test.txt", expectedReadContent.length() - 1, 10000);
        actualReadContent = service1.readFromFile();

        System.out.println("Expected read content: " + expectedReadContent.substring(expectedReadContent.length() - 1));
        System.out.println("Actual read content: " + actualReadContent);

        Assert.assertEquals(expectedReadContent.substring(expectedReadContent.length() - 1), actualReadContent);

        // Clean up
        file.delete();
    }

    @Test()
    public void testReadNonExistentFile() {
        // Test reading from a non-existent file
        Service1 service1 = new Service1("non-existent-file");
        String actualReadContent = service1.readFullFile();

        System.out.println("Expected read content to be null.");
        System.out.println("Actual read content: " + actualReadContent);

        Assert.assertNull(actualReadContent);
    }
}

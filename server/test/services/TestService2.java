package test.services;

import org.junit.Assert;
import org.junit.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;

import src.services.Service2;

public class TestService2 {
    @Test()
    public void testInitializeService2() {
        // Test initialization of Service2
        String expectedFilePath = "test/file/path";
        int expectedOffset = 10;
        byte[] expectedBytesToInsert = new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        Service2 service2 = new Service2(expectedFilePath, expectedOffset, expectedBytesToInsert);
        
        String actualFilePathString = service2.getFilePath();
        int actualOffset = service2.getOffset();
        byte[] actualBytesToInsert = service2.getBytesToInsert();

        System.out.println("Expected file path: " + expectedFilePath);
        System.out.println("Actual file path: " + actualFilePathString);
        System.out.println("Expected offset: " + expectedOffset);
        System.out.println("Actual offset: " + actualOffset);
        System.out.println("Expected bytes to insert: " + expectedBytesToInsert);
        System.out.println("Actual bytes to insert: " + actualBytesToInsert);

        Assert.assertEquals(expectedFilePath, actualFilePathString);
        Assert.assertEquals(expectedOffset, actualOffset);
        Assert.assertArrayEquals(expectedBytesToInsert, actualBytesToInsert);
    }

    @Test()
    public void testWriteToFile() {
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

        // Test writing to the file
        int expectedOffset = 10;
        int expectedStatus = 200;
        byte[] expectedBytesToInsert = "inserted".getBytes();
        Service2 service2 = new Service2(currentDir + File.separator + "test.txt", expectedOffset, expectedBytesToInsert);
        int status = service2.writeToFile();

        // Read the file and check if the bytes are inserted
        byte[] actualBytes = new byte[expectedBytesToInsert.length];
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(expectedOffset);
            raf.read(actualBytes);
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Expected bytes to insert: " + expectedBytesToInsert);
        System.out.println("Actual bytes inserted: " + actualBytes);

        Assert.assertArrayEquals(expectedBytesToInsert, actualBytes);

        // Verify status
        System.out.println("Expected status: " + expectedStatus);
        System.out.println("Actual status: " + status);

        Assert.assertEquals(expectedStatus, status);

        // Clean up
        file.delete();
    }

    @Test()
    public void testMultiWrite() {
        // Test that 2 writes will insert 2 times (i.e., does not overwrite)
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

        // Test writing to the file
        int expectedOffset = 10;
        byte[] expectedBytesToInsert = "inserted".getBytes();
        Service2 service2 = new Service2(currentDir + File.separator + "test.txt", expectedOffset, expectedBytesToInsert);
        int status1 = service2.writeToFile();
        int status2 = service2.writeToFile();

        // Read the file and check if the bytes are inserted
        byte[] actualBytes = new byte[expectedBytesToInsert.length * 2];
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(expectedOffset);
            raf.read(actualBytes);
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] expectedBytes = new byte[expectedBytesToInsert.length * 2];
        System.arraycopy(expectedBytesToInsert, 0, expectedBytes, 0, expectedBytesToInsert.length);
        System.arraycopy(expectedBytesToInsert, 0, expectedBytes, expectedBytesToInsert.length, expectedBytesToInsert.length);

        System.out.println("Expected bytes to insert: " + expectedBytes);
        System.out.println("Actual bytes inserted: " + actualBytes);

        Assert.assertArrayEquals(expectedBytes, actualBytes);

        // Verify file length
        long expectedFileLength = originalContentString.length() + expectedBytesToInsert.length * 2;
        long actualFileLength = file.length();

        System.out.println("Expected file length: " + expectedFileLength);
        System.out.println("Actual file length: " + actualFileLength);

        Assert.assertEquals(expectedFileLength, actualFileLength);

        // Verify status
        int expectedStatus = 200;

        System.out.println("Expected status: " + expectedStatus);
        System.out.println("Actual status 1: " + status1);
        System.out.println("Actual status 2: " + status2);

        Assert.assertEquals(expectedStatus, status1);
        Assert.assertEquals(expectedStatus, status2);

        // Clean up
        file.delete();
    }

    @Test()
    public void testWriteToNonExistentFile() {
        // Test writing to a non-existent file
        int expectedOffset = 10;
        byte[] expectedBytesToInsert = "inserted".getBytes();
        Service2 service2 = new Service2("nonexistent.txt", expectedOffset, expectedBytesToInsert);
        int status = service2.writeToFile();

        // Should return 404
        int expectedStatus = 404;

        System.out.println("Expected status: " + expectedStatus);
        System.out.println("Actual status: " + status);

        Assert.assertEquals(expectedStatus, status);
    }

    @Test()
    public void testWriteWithLargerOffset() {
        // Test offset > file.length
        // Should return 400

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

        // Test writing to the file
        int expectedOffset = 100;
        byte[] expectedBytesToInsert = "inserted".getBytes();
        Service2 service2 = new Service2(currentDir + File.separator + "test.txt", expectedOffset, expectedBytesToInsert);
        int status = service2.writeToFile();

        // Verify status
        int expectedStatus = 400;

        System.out.println("Expected status: " + expectedStatus);
        System.out.println("Actual status: " + status);

        Assert.assertEquals(expectedStatus, status);

        // Clean up
        file.delete();
    }
}

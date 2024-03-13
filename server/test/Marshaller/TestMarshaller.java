package test.marshaller;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import src.marshaller.Marshaller;

public class TestMarshaller {
    @Test()
    public void testUnmarshalMarshallingInt() {
        // Test marshalling of int
        int originalValue = 5;
        byte[] marshalledData = Marshaller.marshal(originalValue);
        int unmarshalledValue = Marshaller.unmarshalInt(marshalledData, 0);

        System.out.println("Original value: " + originalValue);
        System.out.println("Marshalled data: " + Arrays.toString(marshalledData));
        System.out.println("Unmarshalled value: " + unmarshalledValue);

        Assert.assertEquals(originalValue, unmarshalledValue);
    }

    @Test()
    public void testUnmarshalMarshallingString() {
        // Test marshalling of string
        String originalValue = "Hello, World!";
        int originalValueLength = originalValue.length();
        byte[] marshalledData = Marshaller.marshal(originalValue);
        String unmarshalledValue = Marshaller.unmarshalString(marshalledData, 0, originalValueLength);

        System.out.println("Original value: " + originalValue);
        System.out.println("Marshalled data: " + Arrays.toString(marshalledData));
        System.out.println("Unmarshalled value: " + unmarshalledValue);

        Assert.assertEquals(originalValue, unmarshalledValue);
    }

    @Test()
    public void testAppendInt() {
        // Test appending of byte arrays
        int originalData = 1000;
        byte[] marshalledData = Marshaller.marshal(originalData);
        int intToAppend = 4;
        byte[] appendedData = Marshaller.appendInt(marshalledData, intToAppend);
        int unmarshalledValue1 = Marshaller.unmarshalInt(appendedData, 0);
        int unmarshalledValue2 = Marshaller.unmarshalInt(appendedData, 4);

        System.out.println("Original data: " + originalData);
        System.out.println("Int to append: " + intToAppend);
        System.out.println("Appended data (marshalled): " + Arrays.toString(appendedData));
        System.out.println("Unmarshalled value 1 (original): " + unmarshalledValue1);
        System.out.println("Unmarshalled value 2 (appended): " + unmarshalledValue2);

        Assert.assertEquals(unmarshalledValue1, originalData);
        Assert.assertEquals(unmarshalledValue2, intToAppend);
    }

    @Test()
    public void testAppendString() {
        // Test appending of byte arrays
        String originalData = "Hello, World!";
        byte[] marshalledData = Marshaller.marshal(originalData);
        String stringToAppend = "This is a test";
        byte[] appendedData = Marshaller.appendString(marshalledData, stringToAppend);
        String unmarshalledValue1 = Marshaller.unmarshalString(appendedData, 0, originalData.length());
        String unmarshalledValue2 = Marshaller.unmarshalString(appendedData, originalData.length(),
                stringToAppend.length());

        System.out.println("Original data: " + originalData);
        System.out.println("String to append: " + stringToAppend);
        System.out.println("Appended data (marshalled): " + Arrays.toString(appendedData));
        System.out.println("Unmarshalled value 1 (original): " + unmarshalledValue1);
        System.out.println("Unmarshalled value 2 (appended): " + unmarshalledValue2);

        Assert.assertEquals(unmarshalledValue1, originalData);
        Assert.assertEquals(unmarshalledValue2, stringToAppend);
    }
}

#include "Marshaller.h"

/**
 * Marshals a ClientPacket into a byte array using the S1 format.
 * @param clientPacket The ClientPacket to marshal.
 * @return The marshaled byte array.
 */
std::vector<unsigned char> Marshaller::marshalClientPacketS1(
    const ClientPacket& clientPacket) {
    /*
        FORMAT:
        1. requestId (4 bytes)
        2. serviceId (4 bytes)
        3. filepath length (4 bytes)
        4. filepath (variable length)
        5. offset (4 bytes)
        6. numBytes (4 bytes)
     */
    std::vector<unsigned char> byteArray;
    byteArray = appendInt(byteArray, clientPacket.getRequestId());
    byteArray = appendInt(byteArray, clientPacket.getServiceId());
    byteArray = appendInt(byteArray, clientPacket.getFilepath().length());
    byteArray = appendString(byteArray, clientPacket.getFilepath());
    byteArray = appendInt(byteArray, clientPacket.getPayload()->getOffset());
    byteArray = appendInt(byteArray, clientPacket.getPayload()->getNumBytes());
    return byteArray;
}

/**
 * Marshals a ClientPacket into a byte array using the S2 format.
 * @param clientPacket The ClientPacket to marshal.
 * @return The marshaled byte array.
 */
std::vector<unsigned char> Marshaller::marshalClientPacketS2(
    const ClientPacket& clientPacket) {
    /*
        FORMAT:
        1. requestId (4 bytes)
        2. serviceId (4 bytes)
        3. filepath length (4 bytes)
        4. filepath (variable length)
        5. offset (4 bytes)
        6. input length (4 bytes)
        7. input (variable length)
     */
    std::vector<unsigned char> byteArray;
    byteArray = appendInt(byteArray, clientPacket.getRequestId());
    byteArray = appendInt(byteArray, clientPacket.getServiceId());
    byteArray = appendInt(byteArray, clientPacket.getFilepath().length());
    byteArray = appendString(byteArray, clientPacket.getFilepath());
    byteArray = appendInt(byteArray, clientPacket.getPayload()->getOffset());
    byteArray =
        appendInt(byteArray, clientPacket.getPayload()->getInput().length());
    byteArray = appendString(byteArray, clientPacket.getPayload()->getInput());
    return byteArray;
}

/**
 * Marshals a ClientPacket into a byte array using the S3 format.
 * @param clientPacket The ClientPacket to marshal.
 * @return The marshaled byte array.
 */
std::vector<unsigned char> Marshaller::marshalClientPacketS3(
    const ClientPacket& clientPacket) {
    /*
        FORMAT:
        1. requestId (4 bytes)
        2. serviceId (4 bytes)
        3. filepath length (4 bytes)
        4. filepath (variable length)
        5. expirationInterval (8 bytes)
     */
    std::vector<unsigned char> byteArray;
    byteArray = appendInt(byteArray, clientPacket.getRequestId());
    byteArray = appendInt(byteArray, clientPacket.getServiceId());
    byteArray = appendInt(byteArray, clientPacket.getFilepath().length());
    byteArray = appendString(byteArray, clientPacket.getFilepath());
    byteArray = appendLongLong(byteArray,
                               clientPacket.getPayload()->getExpirationTime());

    // std::vector<unsigned char> tmp =
    //     marshal(clientPacket.getPayload()->getExpirationTime());
    // long expirationTime = unmarshalLong(tmp, 0);

    return byteArray;
}

/**
 * Marshals a ClientPacket into a byte array using the S4 format.
 * @param clientPacket The ClientPacket to marshal.
 * @return The marshaled byte array.
 */
std::vector<unsigned char> Marshaller::marshalClientPacketS4(
    const ClientPacket& clientPacket) {
    /*
        FORMAT:
        1. requestId (4 bytes)
        2. serviceId (4 bytes)
        3. filepath length (4 bytes)
        4. filepath (variable length)
     */
    std::vector<unsigned char> byteArray;
    byteArray = appendInt(byteArray, clientPacket.getRequestId());
    byteArray = appendInt(byteArray, clientPacket.getServiceId());
    byteArray = appendInt(byteArray, clientPacket.getFilepath().length());
    byteArray = appendString(byteArray, clientPacket.getFilepath());
    return byteArray;
}

/**
 * Marshals a ClientPacket into a byte array using the S5 format.
 * @param clientPacket The ClientPacket to marshal.
 * @return The marshaled byte array.
 */
std::vector<unsigned char> Marshaller::marshalClientPacketS5(
    const ClientPacket& clientPacket) {
    /*
        FORMAT:
        1. requestId (4 bytes)
        2. serviceId (4 bytes)
        3. filepath length (4 bytes)
        4. filepath (variable length)
        5. offset (4 bytes)
        6. numBytes (4 bytes)
     */
    std::vector<unsigned char> byteArray;
    byteArray = appendInt(byteArray, clientPacket.getRequestId());
    byteArray = appendInt(byteArray, clientPacket.getServiceId());
    byteArray = appendInt(byteArray, clientPacket.getFilepath().length());
    byteArray = appendString(byteArray, clientPacket.getFilepath());
    byteArray = appendInt(byteArray, clientPacket.getPayload()->getOffset());
    byteArray = appendInt(byteArray, clientPacket.getPayload()->getNumBytes());
    return byteArray;
}

/**
 * Marshals an integer into a byte array.
 * @param x The integer to marshal.
 * @return The marshaled byte array.
 */
std::vector<unsigned char> Marshaller::marshal(int x) {
    /*
        assuming big endian
        e.g x = 0x12345678 -> {0x12, 0x34, 0x56, 0x78}
    */
    return std::vector<unsigned char>{static_cast<unsigned char>(x >> 24),
                                      static_cast<unsigned char>(x >> 16),
                                      static_cast<unsigned char>(x >> 8),
                                      static_cast<unsigned char>(x)};
}

/**
 * Marshals a long integer into a byte array.
 * @param x The long integer to marshal.
 * @return The marshaled byte array.
 */
std::vector<unsigned char> Marshaller::marshal(long long x) {
    /*
        assuming big endian
        e.g x = 0x123456789ABCDEF0 -> {0x12, 0x34, 0x56, 0x78, 0x9A, 0xBC, 0xDE,
       0xF0}
    */
    return std::vector<unsigned char>{static_cast<unsigned char>(x >> 56),
                                      static_cast<unsigned char>(x >> 48),
                                      static_cast<unsigned char>(x >> 40),
                                      static_cast<unsigned char>(x >> 32),
                                      static_cast<unsigned char>(x >> 24),
                                      static_cast<unsigned char>(x >> 16),
                                      static_cast<unsigned char>(x >> 8),
                                      static_cast<unsigned char>(x)};
}

/**
 * Marshals a string into a byte array.
 * @param x The string to marshal.
 * @return The marshaled byte array.
 */
std::vector<unsigned char> Marshaller::marshal(const std::string& x) {
    return std::vector<unsigned char>(x.begin(), x.end());
}

/**
 * Unmarshals an integer from a byte array.
 * @param x The byte array to unmarshal from.
 * @param startIndex The starting index of the integer in the byte array.
 * @return The unmarshaled integer.
 */
int Marshaller::unmarshalInt(const std::vector<char>& x, int startIndex) {
    int result = static_cast<unsigned char>(x[startIndex]) << 24 |
                 (static_cast<unsigned char>(x[startIndex + 1]) & 0xFF) << 16 |
                 (static_cast<unsigned char>(x[startIndex + 2]) & 0xFF) << 8 |
                 (static_cast<unsigned char>(x[startIndex + 3]) & 0xFF);
    return result;
}

/**
 * Unmarshals a long integer from a byte array.
 * @param x The byte array to unmarshal from.
 * @param startIndex The starting index of the long integer in the byte array.
 * @return The unmarshaled long integer.
 */
long long Marshaller::unmarshalLong(const std::vector<char>& x,
                                    int startIndex) {
    long long result =
        static_cast<unsigned char>(x[startIndex]) << 56 |
        (static_cast<unsigned char>(x[startIndex + 1]) & 0xFF) << 48 |
        (static_cast<unsigned char>(x[startIndex + 2]) & 0xFF) << 40 |
        (static_cast<unsigned char>(x[startIndex + 3]) & 0xFF) << 32 |
        (static_cast<unsigned char>(x[startIndex + 4]) & 0xFF) << 24 |
        (static_cast<unsigned char>(x[startIndex + 5]) & 0xFF) << 16 |
        (static_cast<unsigned char>(x[startIndex + 6]) & 0xFF) << 8 |
        (static_cast<unsigned char>(x[startIndex + 7]) & 0xFF);
    return result;
}

/**
 * Unmarshals a string from a byte array.
 * @param x The byte array to unmarshal from.
 * @param startIndex The starting index of the string in the byte array.
 * @param length The length of the string in bytes.
 * @return The unmarshaled string.
 */
std::string Marshaller::unmarshalString(const std::vector<char>& x,
                                        int startIndex, int length) {
    return std::string(x.begin() + startIndex, x.begin() + startIndex + length);
}

/**
 * Appends an integer to a byte array.
 * @param byteArray The byte array to append to.
 * @param x The integer to append.
 * @return The new byte array with the integer appended.
 */
std::vector<unsigned char> Marshaller::appendInt(
    const std::vector<unsigned char>& byteArray, int x) {
    std::vector<unsigned char> intBytes = marshal(x);
    std::vector<unsigned char> newByteArray;
    newByteArray.reserve(byteArray.size() + intBytes.size());
    newByteArray.insert(newByteArray.end(), byteArray.begin(), byteArray.end());
    newByteArray.insert(newByteArray.end(), intBytes.begin(), intBytes.end());
    return newByteArray;
}

/**
 * Appends a long integer to a byte array.
 * @param byteArray The byte array to append to.
 * @param x The long integer to append.
 * @return The new byte array with the long integer appended.
 */
std::vector<unsigned char> Marshaller::appendLongLong(
    const std::vector<unsigned char>& byteArray, long long x) {
    std::vector<unsigned char> intBytes = marshal(x);
    std::vector<unsigned char> newByteArray;
    newByteArray.reserve(byteArray.size() + intBytes.size());
    newByteArray.insert(newByteArray.end(), byteArray.begin(), byteArray.end());
    newByteArray.insert(newByteArray.end(), intBytes.begin(), intBytes.end());
    return newByteArray;
}

/**
 * Appends a string to a byte array.
 * @param byteArray The byte array to append to.
 * @param s The string to append.
 * @return The new byte array with the string appended.
 */
std::vector<unsigned char> Marshaller::appendString(
    const std::vector<unsigned char>& byteArray, const std::string& s) {
    std::vector<unsigned char> stringBytes = marshal(s);
    std::vector<unsigned char> newByteArray;
    newByteArray.reserve(byteArray.size() + stringBytes.size());
    newByteArray.insert(newByteArray.end(), byteArray.begin(), byteArray.end());
    newByteArray.insert(newByteArray.end(), stringBytes.begin(),
                        stringBytes.end());
    return newByteArray;
}
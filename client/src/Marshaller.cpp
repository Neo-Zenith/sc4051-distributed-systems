#include "Marshaller.h"

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

std::vector<unsigned char> Marshaller::marshalClientPacketS3(
    const ClientPacket& clientPacket) {
    /*
        FORMAT:
        1. requestId (4 bytes)
        2. serviceId (4 bytes)
        3. filepath length (4 bytes)
        4. filepath (variable length)
        5. monitorInterval (4 bytes)
     */
    std::vector<unsigned char> byteArray;
    byteArray = appendInt(byteArray, clientPacket.getRequestId());
    byteArray = appendInt(byteArray, clientPacket.getServiceId());
    byteArray = appendInt(byteArray, clientPacket.getFilepath().length());
    byteArray = appendString(byteArray, clientPacket.getFilepath());
    byteArray =
        appendInt(byteArray, clientPacket.getPayload()->getMonitorInterval());
    return byteArray;
}

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

std::vector<unsigned char> Marshaller::marshalClientPacketS5(
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

std::vector<unsigned char> Marshaller::marshal(const std::string& x) {
    return std::vector<unsigned char>(x.begin(), x.end());
}

int Marshaller::unmarshalInt(const std::vector<char>& x, int startIndex) {
    int result = static_cast<unsigned char>(x[startIndex]) << 24 |
                 (static_cast<unsigned char>(x[startIndex + 1]) & 0xFF) << 16 |
                 (static_cast<unsigned char>(x[startIndex + 2]) & 0xFF) << 8 |
                 (static_cast<unsigned char>(x[startIndex + 3]) & 0xFF);
    return result;
}

std::string Marshaller::unmarshalString(const std::vector<char>& x,
                                        int startIndex, int length) {
    return std::string(x.begin() + startIndex, x.begin() + startIndex + length);
}

std::vector<unsigned char> Marshaller::appendInt(
    const std::vector<unsigned char>& byteArray, int x) {
    std::vector<unsigned char> intBytes = marshal(x);
    std::vector<unsigned char> newByteArray;
    newByteArray.reserve(byteArray.size() + intBytes.size());
    newByteArray.insert(newByteArray.end(), byteArray.begin(), byteArray.end());
    newByteArray.insert(newByteArray.end(), intBytes.begin(), intBytes.end());
    return newByteArray;
}

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
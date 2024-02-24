#ifndef MARSHALLER_H
#define MARSHALLER_H

#include <string>
#include <vector>

#include "ClientPacket.h"

class Marshaller {
   public:
    static std::vector<unsigned char> marshalClientPacketS1(
        const ClientPacket& clientPacket);
    static std::vector<unsigned char> marshalClientPacketS2(
        const ClientPacket& clientPacket);
    static std::vector<unsigned char> marshalClientPacketS3(
        const ClientPacket& clientPacket);
    static std::vector<unsigned char> marshalClientPacketS4(
        const ClientPacket& clientPacket);
    static std::vector<unsigned char> marshalClientPacketS5(
        const ClientPacket& clientPacket);

    static std::vector<unsigned char> marshal(int x);
    static std::vector<unsigned char> marshal(const std::string& x);

    static int unmarshalInt(const std::vector<char>& x, int startIndex);
    static std::string unmarshalString(const std::vector<char>& x,
                                       int startIndex, int length);

    static std::vector<unsigned char> appendInt(
        const std::vector<unsigned char>& byteArray, int x);
    static std::vector<unsigned char> appendString(
        const std::vector<unsigned char>& byteArray, const std::string& s);
};

#endif  // MARSHALLER_H
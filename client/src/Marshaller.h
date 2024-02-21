#ifndef MARSHALLER_H
#define MARSHALLER_H

#include <string>
#include <vector>

#include "ClientPacket.h"
using namespace std;

class Marshaller {
   public:
    static vector<unsigned char> marshalClientPacketS1(
        const ClientPacket& clientPacket);
    static vector<unsigned char> marshalClientPacketS2(
        const ClientPacket& clientPacket);
    static vector<unsigned char> marshalClientPacketS3(
        const ClientPacket& clientPacket);
    static vector<unsigned char> marshal(int x);
    static vector<unsigned char> marshal(const string& x);
    static int unmarshalInt(const vector<char>& x, int startIndex);
    static string unmarshalString(const vector<char>& x, int startIndex,
                                  int length);
    static vector<unsigned char> appendInt(
        const vector<unsigned char>& byteArray, int x);
    static vector<unsigned char> appendString(
        const vector<unsigned char>& byteArray, const string& s);

   private:
};

#endif
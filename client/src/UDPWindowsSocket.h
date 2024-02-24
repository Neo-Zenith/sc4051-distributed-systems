#ifndef UDP_WINDOWS_SOCKET_H
#define UDP_WINDOWS_SOCKET_H

#include <winsock2.h>
#include <ws2tcpip.h>

#include <iostream>
#include <string>
#include <vector>

class UDPWindowsSocket {
   public:
    UDPWindowsSocket(const std::string& server, int port);

    void sendPacket(const std::vector<unsigned char>& data);

    int receivePacket(std::vector<char>& buffer, int timeoutInSeconds);

    void closeSocket();

   private:
    std::string server_;
    int port_;
    WSADATA wsaData_;
    SOCKET sockfd_;
    sockaddr_in servaddr_;

    bool fillServerInfo();
};

#endif  // UDP_WINDOWS_SOCKET_H

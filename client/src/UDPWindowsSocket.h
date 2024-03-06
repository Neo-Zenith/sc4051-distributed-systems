#ifndef UDP_WINDOWS_SOCKET_H
#define UDP_WINDOWS_SOCKET_H

#include <winsock2.h>
#include <ws2tcpip.h>

#include <iostream>
#include <string>
#include <vector>

/**
 * @class UDPWindowsSocket
 * @brief Represents a UDP socket for Windows.
 *
 * This class provides functionality to send and receive UDP packets using
 * Windows sockets.
 */
class UDPWindowsSocket {
   public:
    /**
     * @brief Constructs a UDPWindowsSocket object.
     * @param server The server address to connect to.
     * @param port The port number to connect to.
     */
    UDPWindowsSocket(const std::string& server, int port);

    /**
     * @brief Sends a UDP packet to the server.
     * @param data The data to be sent as a vector of unsigned characters.
     */
    void sendPacket(const std::vector<unsigned char>& data);

    /**
     * @brief Receives a UDP packet from the server.
     * @param buffer The buffer to store the received data as a vector of
     * characters.
     * @param timeoutInSeconds The timeout duration in seconds for receiving the
     * packet.
     * @return The number of bytes received, or -1 if an error occurred.
     */
    int receivePacket(std::vector<char>& buffer, int timeoutInSeconds);

    /**
     * @brief Closes the UDP socket.
     */
    void closeSocket();

   private:
    std::string server_;    ///< The server address to connect to.
    int port_;              ///< The port number to connect to.
    WSADATA wsaData_;       ///< Windows socket initialization data.
    SOCKET sockfd_;         ///< The socket file descriptor.
    sockaddr_in servaddr_;  ///< The server address structure.

    /**
     * @brief Fills the server information in the sockaddr_in structure.
     * @return True if the server information was successfully filled, false
     * otherwise.
     */
    bool fillServerInfo();
};

#endif  // UDP_WINDOWS_SOCKET_H

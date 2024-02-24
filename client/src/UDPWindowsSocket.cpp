#include "UDPWindowsSocket.h"

/**
 * @brief Constructs a UDPWindowsSocket object with the specified server and
 * port.
 *
 * @param server The server address to connect to.
 * @param port The port number to connect to.
 * @throws std::runtime_error if WSAStartup fails, socket creation fails, or the
 * address is invalid or not supported.
 */
UDPWindowsSocket::UDPWindowsSocket(const std::string& server, int port)
    : server_(server), port_(port) {
    if (WSAStartup(MAKEWORD(2, 2), &wsaData_) != 0) {
        throw std::runtime_error("WSAStartup failed");
    }

    sockfd_ = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
    if (sockfd_ == INVALID_SOCKET) {
        WSACleanup();
        throw std::runtime_error("Socket creation failed");
    }

    if (!fillServerInfo()) {
        closesocket(sockfd_);
        WSACleanup();
        throw std::runtime_error("Invalid address / Address not supported");
    }
}

/**
 * @brief Closes the UDP socket and cleans up the Windows Sockets API.
 */
void UDPWindowsSocket::closeSocket() {
    std::cout << "Closing socket...\n";
    closesocket(sockfd_);
    WSACleanup();
}

/**
 * Sends a packet of data to the server using UDP protocol.
 *
 * @param data The vector containing the data to be sent.
 * @throws std::runtime_error if the sendto() function fails.
 */
void UDPWindowsSocket::sendPacket(const std::vector<unsigned char>& data) {
    if (sendto(sockfd_, reinterpret_cast<const char*>(data.data()), data.size(),
               0, (struct sockaddr*)&servaddr_,
               sizeof(servaddr_)) == SOCKET_ERROR) {
        std::cout << "Error code: " << WSAGetLastError() << std::endl;
        throw std::runtime_error("sendto() failed");
    }

    std::cout << "\nPacket sent to server: " << inet_ntoa(servaddr_.sin_addr)
              << " with port: " << ntohs(servaddr_.sin_port) << std::endl;
}

/**
 * Receives a packet from the socket and stores it in the provided buffer.
 *
 * @param buffer The buffer to store the received packet.
 */
int UDPWindowsSocket::receivePacket(std::vector<char>& buffer,
                                    int timeoutInSeconds) {
    DWORD timeout = timeoutInSeconds * 1000;
    setsockopt(sockfd_, SOL_SOCKET, SO_RCVTIMEO, (char*)&timeout,
               sizeof(timeout));

    socklen_t len = sizeof(servaddr_);
    return recvfrom(sockfd_, reinterpret_cast<char*>(buffer.data()),
                    buffer.size(), 0, (struct sockaddr*)&servaddr_, &len);
}

/**
 * @brief Fills the server information for the UDP socket.
 *
 * This function initializes the server address structure with the provided
 * server IP address and port number.
 *
 * @return true if the server information is successfully filled, false
 * otherwise.
 */
bool UDPWindowsSocket::fillServerInfo() {
    memset(&servaddr_, 0, sizeof(servaddr_));
    servaddr_.sin_family = AF_INET;
    servaddr_.sin_port = htons(port_);
    if (inet_pton(AF_INET, server_.c_str(), &servaddr_.sin_addr) <= 0) {
        return false;
    }
    return true;
}

#include <errno.h>
#include <winsock2.h>
#include <ws2tcpip.h>

#include <iostream>

#include "Cache.h"
#include "ClientPacket.h"
#include "ClientPayload.h"
#include "Marshaller.h"

#define SERVER "127.0.0.1"
#define PORT 2222
#define BUFLEN 2048

using namespace std;

#include <chrono>
#include <unordered_map>

void setReceiveTimeout(SOCKET s, int timeoutInSeconds) {
    // Convert timeout to milliseconds
    int timeoutInMilliseconds = timeoutInSeconds * 1000;

    // Set the receive timeout option
    timeval timeout;
    timeout.tv_sec = timeoutInSeconds;
    timeout.tv_usec = 0;
    setsockopt(s, SOL_SOCKET, SO_RCVTIMEO, (char*)&timeout, sizeof(timeout));
}

void sendPacket(SOCKET s, const sockaddr_in& servaddr,
                vector<unsigned char>* header) {
    char* c_header = reinterpret_cast<char*>((*header).data());
    if (sendto(s, c_header, (*header).size(), 0, (struct sockaddr*)&servaddr,
               sizeof(servaddr)) == SOCKET_ERROR) {
        cout << "sendto() failed: " << WSAGetLastError() << endl;
        return;
    }

    cout << "\nPacket sent to server: " << inet_ntoa(servaddr.sin_addr)
         << " with port: " << ntohs(servaddr.sin_port) << endl;
}

void receivePacket(SOCKET s, sockaddr_in& servaddr, vector<char>* buffer) {
    char* c_buffer = reinterpret_cast<char*>(buffer->data());
    socklen_t len = sizeof(servaddr);
    // Receive the data
    int n = recvfrom(s, c_buffer, buffer->size(), 0,
                     (struct sockaddr*)&servaddr, &len);
}

void service1(SOCKET s, sockaddr_in& servaddr, Cache* cache, int* requestId) {
    cout << "\nRunning service 1 - Read from RFS...\n";
    const int SERVICE_ID = 1;

    string filepath;
    int offset;
    int numBytes;

    cout << "Enter the filepath: ";
    cin >> filepath;

    cout << "Enter the offset: ";
    cin >> offset;

    cout << "Enter the number of bytes: ";
    cin >> numBytes;

    CachedResponse* cachedResponse =
        cache->checkCache(filepath, offset, numBytes);
    if (cachedResponse != nullptr) {
        cout << "Cache hit!\n";
        cout << "Content: " << cachedResponse->content << "\n";
        return;
    }

    // Create the payload
    ClientPayload payload(offset, numBytes);

    // Create the packet and marshal it
    ClientPacket packet(*requestId, SERVICE_ID, filepath, &payload);
    vector<unsigned char> data = Marshaller::marshalClientPacketS1(packet);

    // Buffer to store response
    vector<char> buffer(BUFLEN);

    // Send actual data
    sendPacket(s, servaddr, &data);
    *requestId++;

    // TODO: Implement timeout and retransmission
    // do {
    //     sendPacket(s, servaddr, &data);
    //     receivePacket(s, servaddr, &buffer);
    //     cout << "Received data from server!"
    //          << "\n";
    // } while (errno == EAGAIN || errno == EWOULDBLOCK);

    // Receive data response
    receivePacket(s, servaddr, &buffer);
    cout << "Received data from server!"
         << "\n";

    int responseId = Marshaller::unmarshalInt(buffer, 0);
    int status = Marshaller::unmarshalInt(buffer, 4);
    int responseLength = Marshaller::unmarshalInt(buffer, 8);
    /*
        -12 to remove the first 4 bytes for response ID, 4 bytes for status and
       4 bytes for responseLength
    */
    string content =
        Marshaller::unmarshalString(buffer, 12, responseLength - 12);

    switch (status) {
        case 0:
            cout << "Status: Success\n";
            cout << "Content: " << content << "\n";

            // Update cache
            cache->insertIntoCache(filepath, offset, numBytes, content);
            break;
        case 1:
            cout << "Status: Error\n";
            cout << "Content: " << content << "\n";
            break;
        default:
            cout << "Status: " << status << "\n";
            break;
    }
}

void service2(SOCKET s, sockaddr_in& servaddr, int* requestId) {
    cout << "\nRunning service 2 - Write to RFS...\n";
    const int SERVICE_ID = 2;

    string filepath;
    int offset;
    string input;

    cout << "Enter the filepath: ";
    cin >> filepath;

    cout << "Enter the offset: ";
    cin >> offset;

    cout << "Enter the number of bytes: ";
    cin >> input;

    // Create the payload
    ClientPayload payload(offset, input);

    // Create the packet and marshal it
    ClientPacket packet(*requestId, SERVICE_ID, filepath, &payload);
    vector<unsigned char> data = Marshaller::marshalClientPacketS2(packet);

    // Buffer to store response
    vector<char> buffer(BUFLEN);

    // Send actual data
    sendPacket(s, servaddr, &data);
    *requestId++;

    // TODO: Implement timeout and retransmission
    // Receive data response
    receivePacket(s, servaddr, &buffer);
    cout << "Received data from server!"
         << "\n";

    int responseId = Marshaller::unmarshalInt(buffer, 0);
    int status = Marshaller::unmarshalInt(buffer, 4);
    int responseLength = Marshaller::unmarshalInt(buffer, 8);
    /*
        -12 to remove the first 4 bytes for response ID, 4 bytes for status and
       4 bytes for responseLength
    */
    string content =
        Marshaller::unmarshalString(buffer, 12, responseLength - 12);

    switch (status) {
        case 0:
            cout << "Status: Success\n";
            break;
        case 1:
            cout << "Status: Error\n";
            break;
        default:
            cout << "Status: " << status << "\n";
            break;
    }
    cout << "Content: " << content << "\n";
}

void service3(SOCKET s, sockaddr_in& servaddr, int* requestId) {
    cout << "\nRunning service 1 - Read from RFS...\n";
    const int SERVICE_ID = 3;

    string filepath;
    int monitorInterval;

    cout << "Enter the filepath: ";
    cin >> filepath;

    cout << "Enter the monitor interval (in mins): ";
    cin >> monitorInterval;

    // Create the payload
    ClientPayload payload(monitorInterval);

    // Create the packet and marshal it
    ClientPacket packet(*requestId, SERVICE_ID, filepath, &payload);
    vector<unsigned char> data = Marshaller::marshalClientPacketS1(packet);

    // Buffer to store response
    vector<char> buffer(BUFLEN);

    // Send actual data
    sendPacket(s, servaddr, &data);
    *requestId++;

    // Receive data response
    receivePacket(s, servaddr, &buffer);
    cout << "Received data from server!"
         << "\n";

    int responseId = Marshaller::unmarshalInt(buffer, 0);
    int status = Marshaller::unmarshalInt(buffer, 4);
    int responseLength = Marshaller::unmarshalInt(buffer, 8);
    /*
        -12 to remove the first 4 bytes for response ID, 4 bytes for status and
       4 bytes for responseLength
    */
    string content =
        Marshaller::unmarshalString(buffer, 12, responseLength - 12);

    switch (status) {
        case 0:
            cout << "Status: Success\n";
            break;
        case 1:
            cout << "Status: Error\n";
            break;
        default:
            cout << "Status: " << status << "\n";
            break;
    }
    cout << "Content: " << content << "\n";
}

SOCKET createSocket() {
    WSADATA wsaData;
    SOCKET sockfd = INVALID_SOCKET;

    // Initialize Winsock
    if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) {
        cerr << "WSAStartup failed: " << WSAGetLastError() << endl;
        return INVALID_SOCKET;
    }

    // Creating socket
    if ((sockfd = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) == INVALID_SOCKET) {
        cerr << "Socket creation failed: " << WSAGetLastError() << endl;
        WSACleanup();
        return INVALID_SOCKET;
    }

    return sockfd;
}

bool fillServerInfo(sockaddr_in& servaddr, const char* server, int port) {
    // Clear old data from servaddr
    memset(&servaddr, 0, sizeof(servaddr));

    // Filling server information
    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(port);
    if (inet_pton(AF_INET, server, &servaddr.sin_addr) <= 0) {
        return false;
    }

    return true;
}

int main() {
    SOCKET sockfd = createSocket();
    if (sockfd == INVALID_SOCKET) {
        return 1;
    }

    setReceiveTimeout(sockfd, 120);  // 2 mins timeout

    struct sockaddr_in servaddr;
    if (!fillServerInfo(servaddr, SERVER, PORT)) {
        cerr << "Invalid address / Address not supported" << endl;
        closesocket(sockfd);
        WSACleanup();
        return 1;
    }

    try {
        int choice;
        int requestId = 1;
        Cache cache(10);
        while (true) {
            cout << "----------------------------------";
            cout << "\nWelcome to the RFS Interface\n\n";
            cout << "Please select a service:\n";
            cout << "1. Service 1 - Read from RFS\n";
            cout << "2. Service 2 - Write to RFS\n";
            cout << "3. Service 3 - Monitor for updates from RFS\n";
            cout << "4. Exit\n";
            cout << "Your choice: ";
            cin >> choice;

            switch (choice) {
                case 1:
                    service1(sockfd, servaddr, &cache, &requestId);
                    break;
                case 2:
                    service2(sockfd, servaddr, &requestId);
                    break;
                case 3:
                    service3(sockfd, servaddr, &requestId);
                    return 0;
                case 4:
                    cout << "Exiting...\n";
                    return 0;
                default:
                    cout << "Invalid choice. Please try again.\n";
            }
        }
    } catch (const exception& e) {
        cerr << e.what() << '\n';
    }

    closesocket(sockfd);
    WSACleanup();

    return 0;
}
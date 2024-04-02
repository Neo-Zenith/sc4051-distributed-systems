#include "ServiceHandler.h"

#include <chrono>
#include <ctime>
#include <format>
#include <iomanip>
#include <iostream>
#include <string>

ServiceHandler::ServiceHandler(PacketLossFrequency packetLossFrequency)
    : packetLossFrequency(packetLossFrequency) {
    packetLossCounter = 0;
}

/**
 * Displays the RFS interface menu.
 */
void ServiceHandler::displayInterface() {
    std::cout << "\n-------------------------------------------\n";
    std::cout << "Welcome to the RFS Interface\n\n";
    std::cout << "Please select a service:\n";
    std::cout << "1. Service 1 - Read from RFS file\n";
    std::cout << "2. Service 2 - Write to RFS file\n";
    std::cout << "3. Service 3 - Monitor for updates from RFS file\n";
    std::cout << "4. Service 4 - Get file size from RFS\n";
    std::cout << "5. Service 5 - Delete from RFS file\n";
    std::cout << "6. Exit\n";
    std::cout << "-------------------------------------------\n\n";
    std::cout << "Your choice: ";
}

/**
 * Executes the selected service based on the user's choice.
 *
 * @param choice The user's choice of service.
 * @param s The UDPWindowsSocket object for communication.
 * @param cache The Cache object for caching responses.
 * @param requestId The ID of the request.
 * @return 1 if the user chooses to exit, 0 otherwise.
 */
int ServiceHandler::chooseService(int choice, UDPWindowsSocket s, Cache* cache,
                                  int* requestId) {
    switch (choice) {
        case 1:
            service1(s, cache, requestId);
            break;
        case 2:
            service2(s, requestId);
            break;
        case 3:
            service3(s, requestId);
            break;
        case 4:
            service4(s, requestId);
            break;
        case 5:
            service5(s, requestId);
            break;
        case 6:
            std::cout << "\nExiting...\n";
            return 1;
        default:
            std::cout << "Invalid choice. Please try again.\n";
    }
    return 0;
}

int ServiceHandler::simulatePacketLoss() {
    switch (packetLossFrequency) {
        case PacketLossFrequency::NEVER:
            return 0;
        case PacketLossFrequency::EVERY_2_REQUEST:
            packetLossCounter++;
            if (packetLossCounter % 2 == 0) {
                packetLossCounter = 0;
                std::cout << "Packet loss -> retrying...\n";
                return 1;
            }
            break;
        case PacketLossFrequency::EVERY_4_REQUEST:
            packetLossCounter++;
            if (packetLossCounter % 4 == 0) {
                packetLossCounter = 0;
                std::cout << "Packet loss -> retrying...\n";
                return 1;
            }
            break;
        case PacketLossFrequency::RANDOM:
            if (rand() % 10 == 1) {
                std::cout << "Packet loss -> retrying...\n";
                return 1;
            }
            break;
    }
    return 0;
}

/**
 * Executes Service 1 - Read from RFS.
 *
 * @param s The UDPWindowsSocket object for communication.
 * @param cache The Cache object for caching responses.
 * @param requestId The ID of the request.
 */
void ServiceHandler::service1(UDPWindowsSocket s, Cache* cache,
                              int* requestId) {
    std::cout << "\nRunning service 1 - Read from RFS file...\n";
    const int SERVICE_ID = 1;

    std::string filepath;
    int offset;
    int numBytes;

    std::cout << "Enter the filepath: ";
    std::cin >> filepath;

    std::cout << "Enter the offset: ";
    std::cin >> offset;

    std::cout << "Enter the number of bytes: ";
    std::cin >> numBytes;

    // Check cache
    CachedResponse* cachedResponse =
        cache->checkCache(filepath, offset, numBytes);
    if (cachedResponse != nullptr) {
        std::cout << "Content: " << cachedResponse->content << "\n";
        return;
    }

    // Create the payload
    ClientPayload payload(offset, numBytes);

    // Create the packet and marshal it
    ClientPacket packet(*requestId, SERVICE_ID, filepath, &payload);
    *requestId = *requestId + 1;
    std::vector<unsigned char> data = Marshaller::marshalClientPacketS1(packet);

    // Buffer to store response
    std::vector<char> buffer(BUFLEN);

    // Send actual data and receive response
    int numBytesRecv = 0;
    int retries = 0;
    while (true) {
        // Simulate packet loss
        if (simulatePacketLoss()) {
            continue;
        }
        s.sendPacket(data);
        numBytesRecv = s.receivePacket(buffer, TIMEOUT_DURATION);

        if (numBytesRecv == -1 && WSAGetLastError() == WSAETIMEDOUT) {
            std::cout << "Timeout " << retries + 1 << " -> retrying...\n";
            retries++;

            if (retries == 3) {
                break;
            }

            continue;
        }
        break;
    }

    if (numBytesRecv == -1) {
        std::cout << "Error: No response from server\n";
        return;
    }

    int responseId = Marshaller::unmarshalInt(buffer, 0);
    int status = Marshaller::unmarshalInt(buffer, 4);
    unsigned long long lastModified = Marshaller::unmarshalLongLong(buffer, 8);
    int contentLength = Marshaller::unmarshalInt(buffer, 16);
    std::string content =
        Marshaller::unmarshalString(buffer, 20, contentLength);

    std::cout << "Response ID: " << responseId << "\n";
    switch (status) {
        case 0:
            std::cout << "Status: Error\n";
            std::cout << "Content: " << content << "\n";
            break;
        case 1:
            std::cout << "Status: Success\n";
            std::cout << "Content: " << content << "\n";
            std::cout << "Last modified: " << lastModified << std::endl;

            // Update cache
            cache->insertIntoCache(filepath, offset, numBytes, content);
            break;
        default:
            std::cout << "Status: " << status << "\n";
            break;
    }
}

/**
 * Executes Service 2 - Write to RFS.
 *
 * @param s The UDPWindowsSocket object for communication.
 * @param requestId The ID of the request.
 */
void ServiceHandler::service2(UDPWindowsSocket s, int* requestId) {
    std::cout << "\nRunning service 2 - Write to RFS file...\n";
    const int SERVICE_ID = 2;

    std::string filepath;
    int offset;
    std::string input;

    std::cout << "Enter the filepath: ";
    std::cin >> filepath;

    std::cout << "Enter the offset: ";
    std::cin >> offset;

    std::cout << "Enter the characters to be inserted: ";
    std::cin.ignore();
    std::getline(std::cin, input);

    // Create the payload
    ClientPayload payload(offset, input);

    // Create the packet and marshal it
    ClientPacket packet(*requestId, SERVICE_ID, filepath, &payload);
    *requestId = *requestId + 1;
    std::vector<unsigned char> data = Marshaller::marshalClientPacketS2(packet);

    // Buffer to store response
    std::vector<char> buffer(BUFLEN);

    // Send actual data and receive response
    int numBytesRecv = 0;
    int retries = 0;
    while (true) {
        // Simulate packet loss
        if (simulatePacketLoss()) {
            continue;
        }
        s.sendPacket(data);
        numBytesRecv = s.receivePacket(buffer, TIMEOUT_DURATION);

        if (numBytesRecv == -1 && WSAGetLastError() == WSAETIMEDOUT) {
            std::cout << "Timeout " << retries + 1 << " -> retrying...\n";
            retries++;

            if (retries == 3) {
                break;
            }

            continue;
        }
        break;
    }

    if (numBytesRecv == -1) {
        std::cout << "Error: No response from server\n";
        return;
    }

    int responseId = Marshaller::unmarshalInt(buffer, 0);
    int status = Marshaller::unmarshalInt(buffer, 4);
    int contentLength = Marshaller::unmarshalInt(buffer, 8);
    std::string content =
        Marshaller::unmarshalString(buffer, 12, contentLength);

    std::cout << "Response ID: " << responseId << "\n";
    switch (status) {
        case 0:
            std::cout << "Status: Error\n";
            break;
        case 1:
            std::cout << "Status: Success\n";
            break;
        default:
            std::cout << "Status: " << status << "\n";
            break;
    }
    std::cout << "Content: " << content << "\n";
}

/**
 * Executes Service 3 - Monitor for updates from RFS.
 *
 * @param s The UDPWindowsSocket object for communication.
 * @param requestId The ID of the request.
 */
void ServiceHandler::service3(UDPWindowsSocket s, int* requestId) {
    std::cout << "\nRunning service 3 - Monitor for updates from RFS file...\n";
    const int SERVICE_ID = 3;

    std::string filepath;
    int monitorInterval;

    std::cout << "Enter the filepath: ";
    std::cin >> filepath;

    std::cout << "Enter the monitor interval (in mins): ";
    std::cin >> monitorInterval;

    auto startTime = std::chrono::system_clock::now();
    auto endTime = startTime + std::chrono::minutes(monitorInterval);
    auto endTime_time_t = std::chrono::system_clock::to_time_t(endTime);

    // Requires C++ 13
    // std::cout << "Expiration time: "
    //           << std::put_time(std::localtime(&endTime_time_t),
    //                            "%d-%m-%Y %H-%M-%S")
    //           << std::endl;

    unsigned long long expirationTime =
        std::chrono::duration_cast<std::chrono::milliseconds>(
            endTime.time_since_epoch())
            .count();

    std::cout << "Expiration time: " << expirationTime << std::endl;

    // Create the payload
    ClientPayload payload(expirationTime);

    // Create the packet and marshal it
    ClientPacket packet(*requestId, SERVICE_ID, filepath, &payload);
    *requestId = *requestId + 1;
    std::vector<unsigned char> data = Marshaller::marshalClientPacketS3(packet);

    // Buffer to store response
    std::vector<char> buffer(BUFLEN);

    // Send packet
    int numBytesRecv = 0;
    int retries = 0;
    while (true) {
        // Simulate packet loss
        if (simulatePacketLoss()) {
            continue;
        }
        s.sendPacket(data);
        numBytesRecv = s.receivePacket(buffer, TIMEOUT_DURATION);

        if (numBytesRecv == -1 && WSAGetLastError() == WSAETIMEDOUT) {
            std::cout << "Timeout " << retries + 1 << " -> retrying...\n";
            retries++;

            if (retries == 3) {
                break;
            }

            continue;
        }
        break;
    }

    if (numBytesRecv == -1) {
        std::cout << "Error: No response from server\n";
        return;
    }

    int responseId = Marshaller::unmarshalInt(buffer, 0);
    int status = Marshaller::unmarshalInt(buffer, 4);
    int contentLength = Marshaller::unmarshalInt(buffer, 8);
    std::string content =
        Marshaller::unmarshalString(buffer, 12, contentLength);

    // ACK received
    std::cout << "\nUpdate received from server!\n";
    std::cout << "Response ID: " << responseId << "\n";
    switch (status) {
        case 0:
            std::cout << "Status: Error\n";
            break;
        case 1:
            std::cout << "Status: Success\n";
            break;
        default:
            std::cout << "Status: " << status << "\n";
            break;
    }
    std::cout << "Content: " << content << "\n\n";

    // Block for monitorInterval minutes
    while (std::chrono::system_clock::now() < endTime) {
        std::cout << "Monitoring for updates...\n";
        numBytesRecv = s.receivePacket(buffer, 1);
        if (numBytesRecv < 0) {
            continue;
        }

        int responseId = Marshaller::unmarshalInt(buffer, 0);
        int status = Marshaller::unmarshalInt(buffer, 4);
        int contentLength = Marshaller::unmarshalInt(buffer, 8);
        std::string content =
            Marshaller::unmarshalString(buffer, 12, contentLength);

        std::cout << "\nUpdate received from server!\n";
        std::cout << "Response ID: " << responseId << "\n";
        switch (status) {
            case 0:
                std::cout << "Status: Error\n";
                break;
            case 1:
                std::cout << "Status: Success\n";
                break;
            default:
                std::cout << "Status: " << status << "\n";
                break;
        }
        std::cout << "Content: " << content << "\n\n";
    }
    std::cout << "Monitoring complete!\n";
}

/**
 * Executes Service 4 - Get file size from RFS.
 *
 * @param s The UDPWindowsSocket object for communication.
 * @param requestId The ID of the request.
 */
void ServiceHandler::service4(UDPWindowsSocket s, int* requestId) {
    std::cout << "\nRunning service 4 - Get file size from RFS...\n";
    const int SERVICE_ID = 4;

    std::string filepath;

    std::cout << "Enter the filepath: ";
    std::cin >> filepath;

    // Create the payload
    ClientPayload payload;

    // Create the packet and marshal it
    ClientPacket packet(*requestId, SERVICE_ID, filepath, &payload);
    *requestId = *requestId + 1;
    std::vector<unsigned char> data = Marshaller::marshalClientPacketS4(packet);

    // Buffer to store response
    std::vector<char> buffer(BUFLEN);

    // Send actual data and receive response
    int numBytesRecv = 0;
    int retries = 0;
    while (true) {
        // Simulate packet loss
        if (simulatePacketLoss()) {
            continue;
        }
        s.sendPacket(data);
        numBytesRecv = s.receivePacket(buffer, TIMEOUT_DURATION);

        if (numBytesRecv == -1 && WSAGetLastError() == WSAETIMEDOUT) {
            std::cout << "Timeout " << retries + 1 << "  -> retrying...\n";
            retries++;

            if (retries == 3) {
                break;
            }

            continue;
        }
        break;
    }

    if (numBytesRecv == -1) {
        std::cout << "Error: No response from server\n";
        return;
    }

    int responseId = Marshaller::unmarshalInt(buffer, 0);
    int status = Marshaller::unmarshalInt(buffer, 4);
    long long fileSize = Marshaller::unmarshalLongLong(buffer, 8);
    int contentLength = Marshaller::unmarshalInt(buffer, 16);
    std::string content =
        Marshaller::unmarshalString(buffer, 20, contentLength);

    std::cout << "Response ID: " << responseId << "\n";
    switch (status) {
        case 0:
            std::cout << "Status: Error\n";
            break;
        case 1:
            std::cout << "Status: Success\n";
            break;
        default:
            std::cout << "Status: " << status << "\n";
            break;
    }
    std::cout << "File size: " << fileSize << " bytes\n";
    std::cout << "Content: " << content << "\n";
}

/**
 * Executes Service 5 - Delete from RFS.
 *
 * @param s The UDPWindowsSocket object for communication.
 * @param requestId The ID of the request.
 */
void ServiceHandler::service5(UDPWindowsSocket s, int* requestId) {
    std::cout << "\nRunning service 5 - Delete from RFS file...\n";
    const int SERVICE_ID = 5;

    std::string filepath;
    int offset;
    int numBytes;

    std::cout << "Enter the filepath to delete: ";
    std::cin >> filepath;

    std::cout << "Enter the offset to start delete from: ";
    std::cin >> offset;

    std::cout << "Enter the number of bytes to delete: ";
    std::cin >> numBytes;

    // Create the payload
    ClientPayload payload(offset, numBytes);

    // Create the packet and marshal it
    ClientPacket packet(*requestId, SERVICE_ID, filepath, &payload);
    *requestId = *requestId + 1;
    std::vector<unsigned char> data = Marshaller::marshalClientPacketS5(packet);

    // Buffer to store response
    std::vector<char> buffer(BUFLEN);

    // Send actual data and receive response
    int numBytesRecv = 0;
    int retries = 0;
    while (true) {
        // Simulate packet loss
        if (simulatePacketLoss()) {
            continue;
        }
        s.sendPacket(data);
        numBytesRecv = s.receivePacket(buffer, TIMEOUT_DURATION);

        if (numBytesRecv == -1 && WSAGetLastError() == WSAETIMEDOUT) {
            std::cout << "Timeout " << retries + 1 << " -> retrying...\n";
            retries++;

            if (retries == 3) {
                break;
            }

            continue;
        }
        break;
    }

    if (numBytesRecv == -1) {
        std::cout << "Error: No response from server\n";
        return;
    }

    int responseId = Marshaller::unmarshalInt(buffer, 0);
    int status = Marshaller::unmarshalInt(buffer, 4);
    int contentLength = Marshaller::unmarshalInt(buffer, 8);
    std::string content =
        Marshaller::unmarshalString(buffer, 12, contentLength);

    std::cout << "Response ID: " << responseId << "\n";
    switch (status) {
        case 0:
            std::cout << "Status: Error\n";
            break;
        case 1:
            std::cout << "Status: Success\n";
            break;
        default:
            std::cout << "Status: " << status << "\n";
            break;
    }
    std::cout << "Content: " << content << "\n";
}

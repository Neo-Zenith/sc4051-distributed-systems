#include "ServiceHandler.h"

/**
 * Displays the RFS interface menu.
 */
void ServiceHandler::displayInterface() {
    std::cout << "\n-------------------------------------------\n";
    std::cout << "Welcome to the RFS Interface\n\n";
    std::cout << "Please select a service:\n";
    std::cout << "1. Service 1 - Read from RFS\n";
    std::cout << "2. Service 2 - Write to RFS\n";
    std::cout << "3. Service 3 - Monitor for updates from RFS\n";
    std::cout << "4. Service 4 - Get file size from RFS\n";
    std::cout << "5. Service 5 - Delete from RFS\n";
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

/**
 * Executes Service 1 - Read from RFS.
 *
 * @param s The UDPWindowsSocket object for communication.
 * @param cache The Cache object for caching responses.
 * @param requestId The ID of the request.
 */
void ServiceHandler::service1(UDPWindowsSocket s, Cache* cache,
                              int* requestId) {
    std::cout << "\nRunning service 1 - Read from RFS...\n";
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
        std::cout << "Cache hit!\n";
        std::cout << "Content: " << cachedResponse->content << "\n";
        return;
    }

    // Create the payload
    ClientPayload payload(offset, numBytes);

    // Create the packet and marshal it
    ClientPacket packet(*requestId++, SERVICE_ID, filepath, &payload);
    std::vector<unsigned char> data = Marshaller::marshalClientPacketS1(packet);

    // Buffer to store response
    std::vector<char> buffer(BUFLEN);

    // Send actual data and receive response
    int numBytesRecv = 0;
    int retries = 0;
    while (true) {
        s.sendPacket(data);
        numBytesRecv = s.receivePacket(buffer, 5);

        if (numBytesRecv == -1 && WSAGetLastError() == WSAETIMEDOUT) {
            std::cout << "Timeout " << retries + 1 << "-> retrying...\n";
            retries++;

            if (retries == 3) {
                break;
            }
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

    switch (status) {
        case 0:
            std::cout << "Status: Error\n";
            std::cout << "Content: " << content << "\n";
            break;
        case 1:
            std::cout << "Status: Success\n";
            std::cout << "Content: " << content << "\n";

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
    std::cout << "\nRunning service 2 - Write to RFS...\n";
    const int SERVICE_ID = 2;

    std::string filepath;
    int offset;
    std::string input;

    std::cout << "Enter the filepath: ";
    std::cin >> filepath;

    std::cout << "Enter the offset: ";
    std::cin >> offset;

    std::cout << "Enter the number of bytes: ";
    std::cin >> input;

    // Create the payload
    ClientPayload payload(offset, input);

    // Create the packet and marshal it
    ClientPacket packet(*requestId++, SERVICE_ID, filepath, &payload);
    std::vector<unsigned char> data = Marshaller::marshalClientPacketS2(packet);

    // Buffer to store response
    std::vector<char> buffer(BUFLEN);

    // Send actual data and receive response
    int numBytesRecv = 0;
    int retries = 0;
    while (true) {
        s.sendPacket(data);
        numBytesRecv = s.receivePacket(buffer, 5);

        if (numBytesRecv == -1 && WSAGetLastError() == WSAETIMEDOUT) {
            std::cout << "Timeout " << retries + 1 << "-> retrying...\n";
            retries++;

            if (retries == 3) {
                break;
            }
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
    std::cout << "\nRunning service 3 - Monitor for updates from RFS...\n";
    const int SERVICE_ID = 3;

    std::string filepath;
    int monitorInterval;

    std::cout << "Enter the filepath: ";
    std::cin >> filepath;

    std::cout << "Enter the monitor interval (in mins): ";
    std::cin >> monitorInterval;

    // Create the payload
    ClientPayload payload(monitorInterval);

    // Create the packet and marshal it
    ClientPacket packet(*requestId++, SERVICE_ID, filepath, &payload);
    std::vector<unsigned char> data = Marshaller::marshalClientPacketS3(packet);

    // Buffer to store response
    std::vector<char> buffer(BUFLEN);

    // Send and block for monitorInterval minutes
    s.sendPacket(data);

    int numBytesRecv = 0;
    auto startTime = std::chrono::steady_clock::now();
    auto endTime = startTime + std::chrono::minutes(monitorInterval);

    while (std::chrono::steady_clock::now() < endTime) {
        numBytesRecv = s.receivePacket(buffer, 1000000);
        if (numBytesRecv < 0) {
            continue;
        }

        int responseId = Marshaller::unmarshalInt(buffer, 0);
        int status = Marshaller::unmarshalInt(buffer, 4);
        int contentLength = Marshaller::unmarshalInt(buffer, 8);
        std::string content =
            Marshaller::unmarshalString(buffer, 12, contentLength);

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
    ClientPacket packet(*requestId++, SERVICE_ID, filepath, &payload);
    std::vector<unsigned char> data = Marshaller::marshalClientPacketS4(packet);

    // Buffer to store response
    std::vector<char> buffer(BUFLEN);

    // Send actual data and receive response
    int numBytesRecv = 0;
    int retries = 0;
    while (true) {
        s.sendPacket(data);
        numBytesRecv = s.receivePacket(buffer, 5);

        if (numBytesRecv == -1 && WSAGetLastError() == WSAETIMEDOUT) {
            std::cout << "Timeout " << retries + 1 << "-> retrying...\n";
            retries++;

            if (retries == 3) {
                break;
            }
        }
        break;
    }

    if (numBytesRecv == -1) {
        std::cout << "Error: No response from server\n";
        return;
    }

    int responseId = Marshaller::unmarshalInt(buffer, 0);
    int status = Marshaller::unmarshalInt(buffer, 4);
    int fileSize = Marshaller::unmarshalLong(buffer, 8);
    int contentLength = Marshaller::unmarshalInt(buffer, 8);
    std::string content =
        Marshaller::unmarshalString(buffer, 12, contentLength);

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
    std::cout << "\nRunning service 5 - Delete from RFS...\n";
    const int SERVICE_ID = 5;

    std::string filepath;

    std::cout << "Enter the filepath to delete: ";
    std::cin >> filepath;

    // Create the payload
    ClientPayload payload;

    // Create the packet and marshal it
    ClientPacket packet(*requestId++, SERVICE_ID, filepath, &payload);
    std::vector<unsigned char> data = Marshaller::marshalClientPacketS4(packet);

    // Buffer to store response
    std::vector<char> buffer(BUFLEN);

    // Send actual data and receive response
    int numBytesRecv = 0;
    int retries = 0;
    while (true) {
        s.sendPacket(data);
        numBytesRecv = s.receivePacket(buffer, 5);

        if (numBytesRecv == -1 && WSAGetLastError() == WSAETIMEDOUT) {
            std::cout << "Timeout " << retries + 1 << "-> retrying...\n";
            retries++;

            if (retries == 3) {
                break;
            }
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

#include "ServiceHandler.h"

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
        std::cerr << errno << "\n";
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
        std::cerr << errno << "\n";
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

    // Send actual data and receive response
    // TODO: change to send and block for monitor interval
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
        std::cerr << errno << "\n";
        return;
    }

    int responseId = Marshaller::unmarshalInt(buffer, 0);
    int status = Marshaller::unmarshalInt(buffer, 4);
    int fileSize = Marshaller::unmarshalInt(buffer, 8);

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
    std::cout << "File size: " << fileSize << "\n";
}

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
        std::cerr << errno << "\n";
        return;
    }

    // TODO: create a response packet class
    int responseId = Marshaller::unmarshalInt(buffer, 0);
    int status = Marshaller::unmarshalInt(buffer, 4);

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
}

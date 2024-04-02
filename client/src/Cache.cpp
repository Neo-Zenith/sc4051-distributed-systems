/**
 * @file Cache.cpp
 * @brief Implementation of the Cache class.
 */

#include "Cache.h"

#include <vector>

/**
 * @brief Constructs a Cache object with the specified freshness interval.
 * @param freshnessInterval The freshness interval in minutes.
 */
Cache::Cache(int freshnessInterval) : freshnessInterval(freshnessInterval) {}

/**
 * @brief Checks the cache for a cached response.
 * @param path The path of the response.
 * @param offset The offset of the response.
 * @param numBytes The number of bytes in the response.
 * @param s The UDPWindowsSocket object for communication.
 * @param requestId The ID of the request.
 * @return A pointer to the CachedResponse if found in the cache, nullptr
 * otherwise.
 */
CachedResponse* Cache::checkCache(const std::string& path, int offset,
                                  int numBytes, UDPWindowsSocket s,
                                  int* requestId) {
    std::string hash = generateHash(path, offset, numBytes);
    auto it = cache.find(hash);
    if (it == cache.end()) {
        std::cout << "\nNo cached response for (" << path << ", " << offset
                  << ", " << numBytes << ")\n";
        return nullptr;
    }

    // Check if the cached response is still valid
    if (std::chrono::system_clock::now() - it->second.lastValidated <
        std::chrono::seconds(freshnessInterval)) {
        std::cout << "\nCache hit - within freshness interval!\n";
        return &(it->second);
    }

    unsigned long long lastModifiedServer;

    // Create the payload
    ClientPayload payload;

    // Create the packet and marshal it
    ClientPacket packet(*requestId, 0, path, &payload);
    *requestId = *requestId + 1;
    std::vector<unsigned char> data = Marshaller::marshalClientPacketS0(packet);

    // Buffer to store response
    std::vector<char> buffer(2048);

    // Send actual data and receive response
    int numBytesRecv = 0;
    int retries = 0;
    while (true) {
        s.sendPacket(data);
        numBytesRecv = s.receivePacket(buffer, 5);

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
        std::cout << "Error: Unable to fetch last modified time from server\n";
        return nullptr;
    }

    int responseId = Marshaller::unmarshalInt(buffer, 0);
    int status = Marshaller::unmarshalInt(buffer, 4);
    unsigned long long lastModifiedTime =
        Marshaller::unmarshalLongLong(buffer, 8);
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
            std::cout << "Last modified: " << lastModifiedTime << std::endl;
            break;
        default:
            std::cout << "Status: " << status << "\n";
            break;
    }
    lastModifiedServer = lastModifiedTime;

    if (std::chrono::system_clock::from_time_t(static_cast<std::time_t>(
            lastModifiedServer)) == it->second.lastModifiedClient) {
        std::cout << "\nCache hit - not modified on server!\n";
        it->second.lastValidated = std::chrono::system_clock::now();
        return &(it->second);
    } else if (std::chrono::system_clock::from_time_t(static_cast<std::time_t>(
                   lastModifiedServer)) > it->second.lastModifiedClient) {
        std::cout << "\nCached response for (" << path << ", " << offset << ", "
                  << numBytes << ") expired\n";
        return nullptr;
    }

    // not possible to reach this case
    return nullptr;
}

/**
 * @brief Inserts a response into the cache.
 * @param path The path of the response.
 * @param offset The offset of the response.
 * @param numBytes The number of bytes in the response.
 * @param content The content of the response.
 */
void Cache::insertIntoCache(const std::string& path, int offset, int numBytes,
                            const std::string& content,
                            std::time_t lastModifiedServer) {
    std::string hash = generateHash(path, offset, numBytes);
    CachedResponse response = {
        content,
        std::chrono::system_clock::now(),
        std::chrono::system_clock::from_time_t(lastModifiedServer),
    };
    cache[hash] = response;
}

/**
 * @brief Removes a response from the cache.
 * @param path The path of the response.
 * @param offset The offset of the response.
 * @param numBytes The number of bytes in the response.
 */
void Cache::removeFromCache(const std::string& path, int offset, int numBytes) {
    std::string hash = generateHash(path, offset, numBytes);
    cache.erase(hash);
}

/**
 * @brief Generates a hash for a given path, offset, and number of bytes.
 * @param path The path of the response.
 * @param offset The offset of the response.
 * @param numBytes The number of bytes in the response.
 * @return The generated hash.
 */
std::string Cache::generateHash(const std::string& path, int offset,
                                int numBytes) {
    return path + std::to_string(offset) + std::to_string(numBytes);
}

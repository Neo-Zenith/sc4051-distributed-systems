/**
 * @file Cache.cpp
 * @brief Implementation of the Cache class.
 */

#include "Cache.h"

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
 * @return A pointer to the CachedResponse if found in the cache, nullptr
 * otherwise.
 */
CachedResponse* Cache::checkCache(const std::string& path, int offset,
                                  int numBytes) {
    std::string hash = generateHash(path, offset, numBytes);
    auto it = cache.find(hash);
    if (it == cache.end()) {
        std::cout << "No cached response for (" << path << ", " << offset
                  << ", " << numBytes << ")\n";
        return nullptr;
    }

    // Check if the cached response is still valid
    if (std::chrono::system_clock::now() < it->second.lastValidated) {
        std::cout << "\nCache hit!\n";
        return &(it->second);
    }

    // Make call to get last modified time
    std::cout << "Cached response for (" << path << ", " << offset << ", "
              << numBytes << ") expired\n";

    return &(it->second);
}

// // Create the packet and marshal it
// ClientPacket packet(*requestId, SERVICE_ID, filepath, &payload);
// *requestId = *requestId + 1;
// std::vector<unsigned char> data = Marshaller::marshalClientPacketS1(packet);

// // Buffer to store response
// std::vector<char> buffer(BUFLEN);

// // Send actual data and receive response
// int numBytesRecv = 0;
// int retries = 0;
// while (true) {
//     // Simulate packet loss
//     if (simulatePacketLoss()) {
//         continue;
//     }
//     s.sendPacket(data);
//     numBytesRecv = s.receivePacket(buffer, TIMEOUT_DURATION);

//     if (numBytesRecv == -1 && WSAGetLastError() == WSAETIMEDOUT) {
//         std::cout << "Timeout " << retries + 1 << " -> retrying...\n";
//         retries++;

//         if (retries == 3) {
//             break;
//         }

//         continue;
//     }
//     break;
// }

/**
 * @brief Inserts a response into the cache.
 * @param path The path of the response.
 * @param offset The offset of the response.
 * @param numBytes The number of bytes in the response.
 * @param content The content of the response.
 */
void Cache::insertIntoCache(const std::string& path, int offset, int numBytes,
                            const std::string& content) {
    std::string hash = generateHash(path, offset, numBytes);
    CachedResponse response = {content,
                               std::chrono::system_clock::now() +
                                   std::chrono::minutes(freshnessInterval)};
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

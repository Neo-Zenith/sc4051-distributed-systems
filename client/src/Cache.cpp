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
        return nullptr;
    }

    // Check if the cached response has expired
    if (std::chrono::steady_clock::now() > it->second.expirationTime) {
        return nullptr;
    }

    return &(it->second);
}

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
                               std::chrono::steady_clock::now() +
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

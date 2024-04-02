#ifndef CACHE_H
#define CACHE_H

#include <chrono>
#include <iostream>
#include <string>
#include <unordered_map>

/**
 * @brief Represents a cached response with its content and expiration time.
 */
struct CachedResponse {
    std::string content; /**< The content of the cached response. */
    std::chrono::time_point<std::chrono::system_clock>
        lastValidated; /**< Time when cache entry was last validated. */
    std::chrono::time_point<std::chrono::system_clock>
        lastModifiedClient; /**< Time when cache entry was last modified at
                               server. */
};

/**
 * @brief Represents a cache for storing and retrieving responses.
 */
class Cache {
   public:
    /**
     * @brief Constructs a Cache object with the specified freshness interval.
     * @param freshnessInterval The period (in minutes) to keep a response in
     * the cache.
     */
    Cache(int freshnessInterval);

    /**
     * @brief Checks the cache for a cached response matching the specified
     * path, offset, and number of bytes.
     * @param path The path of the requested resource.
     * @param offset The offset within the resource.
     * @param numBytes The number of bytes to retrieve.
     * @return A pointer to the CachedResponse if found, nullptr otherwise.
     */
    CachedResponse* checkCache(const std::string& path, int offset,
                               int numBytes);

    /**
     * @brief Inserts a response into the cache with the specified path, offset,
     * number of bytes, and content.
     * @param path The path of the requested resource.
     * @param offset The offset within the resource.
     * @param numBytes The number of bytes to insert.
     * @param content The content of the response to insert.
     */
    void insertIntoCache(const std::string& path, int offset, int numBytes,
                         const std::string& content);

    /**
     * @brief Removes a response from the cache with the specified path, offset,
     * and number of bytes.
     * @param path The path of the requested resource.
     * @param offset The offset within the resource.
     * @param numBytes The number of bytes to remove.
     */
    void removeFromCache(const std::string& path, int offset, int numBytes);

   private:
    std::unordered_map<std::string, CachedResponse>
        cache;             /**< The cache storing the responses. */
    int freshnessInterval; /**< The period (in minutes) to keep a response in
                              the cache. */

    /**
     * @brief Generates a hash for the specified path, offset, and number of
     * bytes.
     * @param path The path of the requested resource.
     * @param offset The offset within the resource.
     * @param numBytes The number of bytes.
     * @return The generated hash.
     */
    std::string generateHash(const std::string& path, int offset, int numBytes);
};

#endif  // CACHE_H

#ifndef CACHE_H
#define CACHE_H

#include <chrono>
#include <string>
#include <unordered_map>

struct CachedResponse {
    std::string content;
    std::chrono::time_point<std::chrono::steady_clock> expirationTime;
};

class Cache {
   public:
    Cache(int freshnessInterval);
    CachedResponse* checkCache(const std::string& path, int offset,
                               int numBytes);
    void insertIntoCache(const std::string& path, int offset, int numBytes,
                         const std::string& content);
    void removeFromCache(const std::string& path, int offset, int numBytes);

   private:
    std::unordered_map<std::string, CachedResponse> cache;
    int freshnessInterval;  // period to keep response in cache (in mins)

    std::string generateHash(const std::string& path, int offset, int numBytes);
};

#endif  // CACHE_H

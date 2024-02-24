#include "Cache.h"

Cache::Cache(int freshnessInterval) : freshnessInterval(freshnessInterval) {}

CachedResponse* Cache::checkCache(const std::string& path, int offset,
                                  int numBytes) {
    std::string hash = generateHash(path, offset, numBytes);
    auto it = cache.find(hash);
    if (it == cache.end()) {
        return nullptr;
    }

    // expired
    if (std::chrono::steady_clock::now() > it->second.expirationTime) {
        return nullptr;
    }

    return &(it->second);
}

void Cache::insertIntoCache(const std::string& path, int offset, int numBytes,
                            const std::string& content) {
    std::string hash = generateHash(path, offset, numBytes);
    CachedResponse response = {content,
                               std::chrono::steady_clock::now() +
                                   std::chrono::minutes(freshnessInterval)};
    cache[hash] = response;
}

void Cache::removeFromCache(const std::string& path, int offset, int numBytes) {
    std::string hash = generateHash(path, offset, numBytes);
    cache.erase(hash);
}

std::string Cache::generateHash(const std::string& path, int offset,
                                int numBytes) {
    return path + std::to_string(offset) + std::to_string(numBytes);
}

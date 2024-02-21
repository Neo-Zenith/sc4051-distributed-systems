#include "Cache.h"

using namespace std;

Cache::Cache(int freshnessInterval) : freshnessInterval(freshnessInterval) {}

CachedResponse* Cache::checkCache(const string& path, int offset,
                                  int numBytes) {
    string hash = generateHash(path, offset, numBytes);
    auto it = cache.find(hash);
    if (it == cache.end()) {
        return nullptr;
    }

    // expired
    if (chrono::system_clock::now() > it->second.expirationTime) {
        return nullptr;
    }

    return &(it->second);
}

void Cache::insertIntoCache(const string& path, int offset, int numBytes,
                            const string& content) {
    string hash = generateHash(path, offset, numBytes);
    CachedResponse response = {content, chrono::system_clock::now() +
                                            chrono::minutes(freshnessInterval)};
    cache[hash] = response;
}

void Cache::removeFromCache(const string& path, int offset, int numBytes) {
    string hash = generateHash(path, offset, numBytes);
    cache.erase(hash);
}

string Cache::generateHash(const string& path, int offset, int numBytes) {
    return path + to_string(offset) + to_string(numBytes);
}

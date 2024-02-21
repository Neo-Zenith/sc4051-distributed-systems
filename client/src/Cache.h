#include <chrono>
#include <string>
#include <unordered_map>

using namespace std;

struct CachedResponse {
    string content;
    chrono::time_point<chrono::system_clock> expirationTime;
};

class Cache {
   public:
    Cache(int freshnessInterval);
    CachedResponse* checkCache(const string& path, int offset, int numBytes);
    void insertIntoCache(const string& path, int offset, int numBytes,
                         const string& content);
    void removeFromCache(const string& path, int offset, int numBytes);

   private:
    unordered_map<string, CachedResponse> cache;
    int freshnessInterval;  // period to keep response in cache (in mins)

    string generateHash(const string& path, int offset, int numBytes);
};
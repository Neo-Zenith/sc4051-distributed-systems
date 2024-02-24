#ifndef CLIENT_PAYLOAD_H
#define CLIENT_PAYLOAD_H

#include <string>

class ClientPayload {
    int offset;
    int numBytes;
    int monitorInterval;  // in minutes
    std::string input;

   public:
    // Constructor
    ClientPayload();
    ClientPayload(int monitorInterval);
    ClientPayload(int offset, int numBytes);
    ClientPayload(int offset, const std::string& input);

    // Getters
    int getOffset() const;
    int getNumBytes() const;
    int getMonitorInterval() const;
    const std::string& getInput() const;

    // Setters
    void setOffset(int offset);
    void setNumBytes(int numBytes);
    void setInput(const std::string& input);
    void setMonitorInterval(int monitorInterval);
};

#endif  // CLIENT_PAYLOAD_H
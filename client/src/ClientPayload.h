#ifndef CLIENTPAYLOAD_H
#define CLIENTPAYLOAD_H

#include <string>
using namespace std;

class ClientPayload {
    int offset;
    int numBytes;
    int monitorInterval;  // in minutes
    string input;

   public:
    // Constructor
    ClientPayload(int offset, int numBytes);
    ClientPayload(int offset, const string& input);
    ClientPayload(int monitorInterval);

    // Getters
    int getOffset() const;
    int getNumBytes() const;
    int getMonitorInterval() const;
    const string& getInput() const;

    // Setters
    void setOffset(int offset);
    void setNumBytes(int numBytes);
    void setInput(const string& input);
    void setMonitorInterval(int monitorInterval);
};

#endif
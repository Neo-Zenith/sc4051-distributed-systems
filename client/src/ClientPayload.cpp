#include "ClientPayload.h"

// Constructor
ClientPayload::ClientPayload() {}
ClientPayload::ClientPayload(int monitorInterval)
    : monitorInterval(monitorInterval) {}
ClientPayload::ClientPayload(int offset, int numBytes)
    : offset(offset), numBytes(numBytes) {}
ClientPayload::ClientPayload(int offset, const std::string& input)
    : offset(offset), input(input) {}

// Getters
int ClientPayload::getOffset() const { return offset; }
int ClientPayload::getNumBytes() const { return numBytes; }
int ClientPayload::getMonitorInterval() const { return monitorInterval; }
const std::string& ClientPayload::getInput() const { return input; }

// Setters
void ClientPayload::setOffset(int offset) { this->offset = offset; }
void ClientPayload::setNumBytes(int numBytes) { this->numBytes = numBytes; }
void ClientPayload::setMonitorInterval(int monitorInterval) {
    this->monitorInterval = monitorInterval;
}
void ClientPayload::setInput(const std::string& input) { this->input = input; }
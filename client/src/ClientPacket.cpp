#include "ClientPacket.h"

// Constructor
ClientPacket::ClientPacket(int requestId, int serviceId,
                           const std::string& filepath, ClientPayload* payload)
    : requestId(requestId),
      serviceId(serviceId),
      filepath(filepath),
      payload(payload) {}

// Getters
int ClientPacket::getRequestId() const { return requestId; }
int ClientPacket::getServiceId() const { return serviceId; }
const std::string& ClientPacket::getFilepath() const { return filepath; }
ClientPayload* ClientPacket::getPayload() const { return payload; }

// Setters
void ClientPacket::setRequestId(int requestId) { this->requestId = requestId; }
void ClientPacket::setServiceId(int serviceId) { this->serviceId = serviceId; }
void ClientPacket::setFilepath(const std::string& filepath) {
    this->filepath = filepath;
}
void ClientPacket::setPayload(ClientPayload* payload) {
    this->payload = payload;
}
#include "ClientPacket.h"

/**
 * @brief Constructs a ClientPacket object.
 *
 * This constructor initializes a ClientPacket object with the provided
 * parameters.
 *
 * @param requestId The ID of the request.
 * @param serviceId The ID of the service.
 * @param filepath The filepath associated with the packet.
 * @param payload A pointer to the ClientPayload object.
 */
ClientPacket::ClientPacket(int requestId, int serviceId,
                           const std::string& filepath, ClientPayload* payload)
    : requestId(requestId),
      serviceId(serviceId),
      filepath(filepath),
      payload(payload) {}

/**
 * @brief Gets the request ID of the ClientPacket.
 *
 * @return The request ID.
 */
int ClientPacket::getRequestId() const { return requestId; }

/**
 * @brief Gets the service ID of the ClientPacket.
 *
 * @return The service ID.
 */
int ClientPacket::getServiceId() const { return serviceId; }

/**
 * @brief Gets the filepath associated with the ClientPacket.
 *
 * @return The filepath.
 */
const std::string& ClientPacket::getFilepath() const { return filepath; }

/**
 * @brief Gets the payload of the ClientPacket.
 *
 * @return A pointer to the ClientPayload object.
 */
ClientPayload* ClientPacket::getPayload() const { return payload; }

// Setters
/**
 * @brief Sets the request ID of the ClientPacket.
 *
 * @param requestId The request ID to set.
 */
void ClientPacket::setRequestId(int requestId) { this->requestId = requestId; }

/**
 * @brief Sets the service ID of the ClientPacket.
 *
 * @param serviceId The service ID to set.
 */
void ClientPacket::setServiceId(int serviceId) { this->serviceId = serviceId; }

/**
 * @brief Sets the filepath associated with the ClientPacket.
 *
 * @param filepath The filepath to set.
 */
void ClientPacket::setFilepath(const std::string& filepath) {
    this->filepath = filepath;
}

/**
 * @brief Sets the payload of the ClientPacket.
 *
 * @param payload A pointer to the ClientPayload object to set.
 */
void ClientPacket::setPayload(ClientPayload* payload) {
    this->payload = payload;
}
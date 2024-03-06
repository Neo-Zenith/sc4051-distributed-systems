#ifndef CLIENT_PACKET_H
#define CLIENT_PACKET_H

#include <string>

#include "ClientPayload.h"

/**
 * @brief Represents a packet sent by the client.
 *
 * This class encapsulates the information required for a client to send a
 * packet to the server. It contains the request ID, service ID, filepath, and
 * payload of the packet.
 */
class ClientPacket {
    int requestId;
    int serviceId;
    std::string filepath;
    ClientPayload* payload;

   public:
    /**
     * @brief Constructs a new ClientPacket object.
     *
     * @param requestId The ID of the request.
     * @param serviceId The ID of the service.
     * @param filepath The filepath associated with the packet.
     * @param payload The payload of the packet.
     */
    ClientPacket(int requestId, int serviceId, const std::string& filepath,
                 ClientPayload* payload);

    // Getters
    /**
     * @brief Gets the request ID of the packet.
     *
     * @return The request ID.
     */
    int getRequestId() const;

    /**
     * @brief Gets the service ID of the packet.
     *
     * @return The service ID.
     */
    int getServiceId() const;

    /**
     * @brief Gets the filepath associated with the packet.
     *
     * @return The filepath.
     */
    const std::string& getFilepath() const;

    /**
     * @brief Gets the payload of the packet.
     *
     * @return A pointer to the payload.
     */
    ClientPayload* getPayload() const;

    // Setters
    /**
     * @brief Sets the request ID of the packet.
     *
     * @param requestId The request ID to set.
     */
    void setRequestId(int requestId);

    /**
     * @brief Sets the service ID of the packet.
     *
     * @param serviceId The service ID to set.
     */
    void setServiceId(int serviceId);

    /**
     * @brief Sets the filepath associated with the packet.
     *
     * @param filepath The filepath to set.
     */
    void setFilepath(const std::string& filepath);

    /**
     * @brief Sets the payload of the packet.
     *
     * @param payload The payload to set.
     */
    void setPayload(ClientPayload* payload);
};

#endif  // CLIENT_PACKET_H
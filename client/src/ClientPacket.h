#ifndef CLIENTPACKET_H
#define CLIENTPACKET_H

#include <string>

#include "ClientPayload.h"

class ClientPacket {
    int requestId;
    int serviceId;
    std::string filepath;
    ClientPayload* payload;  // Use a pointer here

   public:
    // Constructor
    ClientPacket(int requestId, int serviceId, const std::string& filepath,
                 ClientPayload* payload);

    // Getters
    int getRequestId() const;
    int getServiceId() const;
    const std::string& getFilepath() const;
    ClientPayload* getPayload() const;

    // Setters
    void setRequestId(int requestId);
    void setServiceId(int serviceId);
    void setFilepath(const std::string& filepath);
    void setPayload(ClientPayload* payload);
};

#endif
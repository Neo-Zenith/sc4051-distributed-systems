#ifndef SERVICE_HANDLER_H
#define SERVICE_HANDLER_H

#include "Cache.h"
#include "ClientPacket.h"
#include "ClientPayload.h"
#include "Marshaller.h"
#include "UDPWindowsSocket.h"

#define BUFLEN 2048

class ServiceHandler {
   public:
    ServiceHandler() {}
    void service1(UDPWindowsSocket s, Cache* cache, int* requestId);
    void service2(UDPWindowsSocket s, int* requestId);
    void service3(UDPWindowsSocket s, int* requestId);
    void service4(UDPWindowsSocket s, int* requestId);
    void service5(UDPWindowsSocket s, int* requestId);
};

#endif  // SERVICE_HANDLER_H

#ifndef SERVICE_HANDLER_H
#define SERVICE_HANDLER_H

#include "Cache.h"
#include "ClientPacket.h"
#include "ClientPayload.h"
#include "Marshaller.h"
#include "UDPWindowsSocket.h"

#define BUFLEN 2048

/**
 * @class ServiceHandler
 * @brief Handles various services provided by the client.
 *
 * The ServiceHandler class provides methods to handle different services
 * provided by the client. It interacts with the UDPWindowsSocket class to
 * send and receive data over UDP sockets. It also uses the Cache class to
 * store and retrieve cached data.
 */
class ServiceHandler {
    static constexpr int TIMEOUT_DURATION = 5;  // Timeout duration in seconds

   public:
    /**
     * @brief Displays the interface for selecting a service.
     */
    void displayInterface();

    /**
     * @brief Chooses a service based on the user's choice.
     * @param choice The user's choice of service.
     * @param s The UDPWindowsSocket object for sending and receiving data.
     * @param cache The Cache object for storing and retrieving cached data.
     * @param requestId Pointer to the request ID.
     * @return The status code indicating the success or failure of the service.
     */
    int chooseService(int choice, UDPWindowsSocket s, Cache* cache,
                      int* requestId);

    /**
     * @brief Simulates packet loss.
     * @return The status code indicating the success or failure of packet
     * delivery.
     */
    int simulatePacketLoss();

    /**
     * @brief Performs service 1.
     * @param s The UDPWindowsSocket object for sending and receiving data.
     * @param cache The Cache object for storing and retrieving cached data.
     * @param requestId Pointer to the request ID.
     */
    void service1(UDPWindowsSocket s, Cache* cache, int* requestId);

    /**
     * @brief Performs service 2.
     * @param s The UDPWindowsSocket object for sending and receiving data.
     * @param requestId Pointer to the request ID.
     */
    void service2(UDPWindowsSocket s, int* requestId);

    /**
     * @brief Performs service 3.
     * @param s The UDPWindowsSocket object for sending and receiving data.
     * @param requestId Pointer to the request ID.
     */
    void service3(UDPWindowsSocket s, int* requestId);

    /**
     * @brief Performs service 4.
     * @param s The UDPWindowsSocket object for sending and receiving data.
     * @param requestId Pointer to the request ID.
     */
    void service4(UDPWindowsSocket s, int* requestId);

    /**
     * @brief Performs service 5.
     * @param s The UDPWindowsSocket object for sending and receiving data.
     * @param requestId Pointer to the request ID.
     */
    void service5(UDPWindowsSocket s, int* requestId);
};

#endif  // SERVICE_HANDLER_H

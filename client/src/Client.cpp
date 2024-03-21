#include <errno.h>
#include <winsock2.h>
#include <ws2tcpip.h>

#include <iostream>

#include "ServiceHandler.h"
#include "UDPWindowsSocket.h"

#define SERVER "127.0.0.1"
#define PORT 2222

/**
 * @brief The main function of the client application.
 *
 * @param argc The number of command-line arguments.
 * @param argv An array of command-line arguments.
 * @return int The exit status of the program.
 */
int main(int argc, char* argv[]) {
    if (argc != 4) {
        std::cout
            << "Usage: " << argv[0]
            << "<server_addr> <freshness_interval> <packet_loss_frequency>\n";
        return 1;
    }

    std::string server_addr = argv[1];
    if (server_addr == "localhost") {
        server_addr = SERVER;
    }
    int freshnessInterval = atoi(argv[2]);
    PacketLossFrequency packetLossFrequency =
        PacketLossFrequency(atoi(argv[3]));

    UDPWindowsSocket udpSocket(server_addr, PORT);
    ServiceHandler handler(packetLossFrequency);

    try {
        int choice;
        int requestId = 1;
        int quit = 0;
        Cache cache(freshnessInterval);
        while (!quit) {
            handler.displayInterface();
            std::cin >> choice;

            quit = handler.chooseService(choice, udpSocket, &cache, &requestId);
            std::cin.clear();
        }
    } catch (const std::exception& e) {
        std::cerr << e.what() << '\n';
    }

    udpSocket.closeSocket();
    return 0;
}

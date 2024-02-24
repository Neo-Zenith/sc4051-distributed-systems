#include <errno.h>
#include <winsock2.h>
#include <ws2tcpip.h>

#include <iostream>

#include "ServiceHandler.h"
#include "UDPWindowsSocket.h"

#define SERVER "127.0.0.1"
#define PORT 2222

using namespace std;

void displayInterface() {
    cout << "\n----------------------------------";
    cout << "\nWelcome to the RFS Interface\n\n";
    cout << "Please select a service:\n";
    cout << "1. Service 1 - Read from RFS\n";
    cout << "2. Service 2 - Write to RFS\n";
    cout << "3. Service 3 - Monitor for updates from RFS\n";
    cout << "4. Service 4 - Get file size from RFS\n";
    cout << "5. Service 5 - Delete from RFS\n";
    cout << "6. Exit\n\n";
    cout << "Your choice: ";
}

int main() {
    UDPWindowsSocket udpSocket(SERVER, PORT);
    ServiceHandler handler;

    try {
        int choice;
        int requestId = 1;
        Cache cache(10);
        while (true) {
            displayInterface();
            cin >> choice;

            switch (choice) {
                case 1:
                    handler.service1(udpSocket, &cache, &requestId);
                    break;
                case 2:
                    handler.service2(udpSocket, &requestId);
                    break;
                case 3:
                    handler.service3(udpSocket, &requestId);
                    return 0;
                case 4:
                    handler.service4(udpSocket, &requestId);
                    return 0;
                case 5:
                    handler.service5(udpSocket, &requestId);
                    return 0;
                case 6:
                    cout << "Exiting...\n";
                    return 0;
                default:
                    cout << "Invalid choice. Please try again.\n";
            }
        }
    } catch (const exception& e) {
        cerr << e.what() << '\n';
    }

    udpSocket.closeSocket();
    return 0;
}

#include <sys/socket.h>
#include <netinet/in.h>

// Create a UDP server socket
int createServerSocket() {
    int serverSocket;
    serverSocket = socket(AF_INET, SOCK_DGRAM, 0);
    if (serverSocket < 0) {
        perror("Error opening socket");
        exit(1);
    }
    return serverSocket;
}

// Bind the server socket to the server address
void bindServerSocket(int serverSocket, struct sockaddr_in serverAddress) {
    int bindStatus;
    bindStatus = bind(serverSocket, (struct sockaddr *) &serverAddress, sizeof(serverAddress));
    if (bindStatus < 0) {
        perror("Error binding socket");
        exit(1);
    }
}
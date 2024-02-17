// Build a server that allows client to remotely access a file
// When server receives the request, it reads the file and sends it to the client
// Data is sent as marshalled data
#include "./headers/reader.h"
#include <stdio.h>

int main() {
    char *filePath = "./test.txt";
    reader(filePath, 6, 15);
    return 0;
}
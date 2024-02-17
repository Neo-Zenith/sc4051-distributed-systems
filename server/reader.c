#include <stdio.h>

int reader(char *pathName, int offset, int numBytes) {
    FILE *file;

    // Open file
    file = fopen(pathName, "r");
    // Check if file exists
    if (file == NULL) {
        printf("Error: File not found\n");
        return(1);
    }
    fseek(file, offset, SEEK_SET);
    
    int i = 0;
    while (i < numBytes) {
        char c = fgetc(file);
        if (feof(file)) {
            break;
        }
        printf("%c", c);
        i ++;
    }
    fclose(file);
    
    return(0);
}
#include <stdio.h>

int reader(char *pathName, int offset, int numBytes) {
    FILE *file;

    file = fopen(pathName, "r");
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
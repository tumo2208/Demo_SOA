// client_loop.c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#define PORT 8000

int main() {
    while (1) {
        int sockfd;
        struct sockaddr_in serv_addr;
        char buffer[256];

        int a, b;
        printf("Enter two numbers to add (or q to quit): ");
        if (scanf("%d %d", &a, &b) != 2) {
            printf("Exiting...\n");
            break;
        }

        /*
            Having to manually handle connection explicitly
            RPC hides all of this, client code would looks like local function call.
        */
        sockfd = socket(AF_INET, SOCK_STREAM, 0);
        if (sockfd < 0) { perror("socket"); exit(1); }

        memset(&serv_addr, 0, sizeof(serv_addr));
        serv_addr.sin_family = AF_INET;
        serv_addr.sin_port = htons(PORT);
        inet_pton(AF_INET, "127.0.0.1", &serv_addr.sin_addr);

        if (connect(sockfd, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0) {
            perror("connect");
            exit(1);
        }

        /*
            1. 
            Manual message formatting in non-RPC.
            Client have to manually convert the numbers into string.
            RPC would automatically marshal arguments into a message
        */
        snprintf(buffer, sizeof(buffer), "%d %d", a, b);
        write(sockfd, buffer, strlen(buffer));

        memset(buffer, 0, sizeof(buffer));
        read(sockfd, buffer, sizeof(buffer)-1);
        printf("Server replied: %s\n\n", buffer);

        close(sockfd);
    }

    return 0;
}

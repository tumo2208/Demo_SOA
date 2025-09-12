#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <arpa/inet.h>

#define PORT 8000

int main() {
    int server_fd = socket(AF_INET, SOCK_STREAM, 0);

    struct sockaddr_in addr = {0};
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = INADDR_ANY;
    addr.sin_port = htons(PORT);


    bind(server_fd, (struct sockaddr*)&addr, sizeof(addr));
    listen(server_fd, 1);

    printf("Server listening on port %d", PORT);
    while (1) {
        int client_fd = accept(server_fd, NULL, NULL);
        if (client_fd < 0) continue;

        char buf[256];
        read(client_fd, buf, sizeof(buf));

        int a, b;
        /*
            2. 
            Server has to manually parse the string
            RPC stubs would automatically unmarhsal arguments into native types
        */
        sscanf(buf, "%d %d", &a, &b);
        sprintf(buf, "RESULT %d", a + b);
        write(client_fd, buf, strlen(buf));

        close(client_fd);
    }
}

#!/bin/bash
echo "Starting RMI Client..."
echo "Make sure the server is running first!"
echo ""

# Check if compiled
if [ ! -f "Client.class" ]; then
    echo "Please compile first: ./compile.sh"
    exit 1
fi

# Start the client
java Client

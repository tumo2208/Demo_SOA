#!/bin/bash
echo "Starting RMI Server..."
echo "Make sure you have compiled first with: ./compile.sh"
echo ""

# Check if compiled
if [ ! -f "Server.class" ]; then
    echo "Please compile first: ./compile.sh"
    exit 1
fi

# Start the server
java Server

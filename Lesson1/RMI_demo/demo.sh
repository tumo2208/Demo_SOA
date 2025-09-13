#!/bin/bash
echo "=== RMI Demo Script ==="
echo "This will run a complete RMI demonstration"
echo ""

# Compile
echo "1. Compiling..."
./compile.sh
if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo ""
echo "2. Starting server in background..."
java Server &
SERVER_PID=$!
echo "Server PID: $SERVER_PID"

# Wait for server to start
echo "Waiting for server to start..."
sleep 3

echo ""
echo "3. Running client..."
java Client

echo ""
echo "4. Stopping server..."
kill $SERVER_PID 2>/dev/null
echo "Demo complete!"

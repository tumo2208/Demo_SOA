#!/bin/bash
# RMI Example Build and Run Scripts

echo "Creating RMI project structure..."

# Create compile script
cat > compile.sh << 'EOF'
#!/bin/bash
echo "Compiling RMI example..."
javac *.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "Files compiled:"
    ls -la *.class
else
    echo "Compilation failed!"
    exit 1
fi
EOF

# Create server run script
cat > run_server.sh << 'EOF'
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
EOF

# Create client run script  
cat > run_client.sh << 'EOF'
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
EOF

# Create demo script
cat > demo.sh << 'EOF'
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
EOF

# Create README
cat > README.md << 'EOF'
# Simple RMI Example

This is a basic RMI (Remote Method Invocation) example with a Calculator service.

## Files:
- `Calculator.java` - Remote interface
- `CalculatorImpl.java` - Remote object implementation  
- `Server.java` - RMI server application
- `Client.java` - RMI client application

## How to Run:

### Method 1: Manual (Recommended)
```bash
# 1. Compile
./compile.sh

# 2. Start server (Terminal 1)
./run_server.sh

# 3. Start client (Terminal 2) 
./run_client.sh
```

### Method 2: Automated Demo
```bash
./demo.sh
```

## What it demonstrates:
- Remote interface definition
- Server-side object implementation
- Client-side remote object lookup
- Remote method invocation
- RMI registry usage
- Error handling for distributed systems

## Requirements:
- Java 11+ (tested with OpenJDK 11.0.27)
- Network access to localhost:1099

## Architecture:
```
Client.java  -->  RMI Registry  -->  Server.java
                 (port 1099)        (CalculatorImpl)
```

The client looks up the Calculator object in the RMI registry and calls methods on it. The server creates the registry, binds the Calculator implementation, and serves remote method calls.
EOF

# Make scripts executable
chmod +x compile.sh run_server.sh run_client.sh demo.sh

echo ""
echo "=== RMI Example Setup Complete! ==="
echo ""
echo "Files created:"
echo "- Calculator.java (interface)"
echo "- CalculatorImpl.java (implementation)" 
echo "- Server.java (server app)"
echo "- Client.java (client app)"
echo "- Scripts: compile.sh, run_server.sh, run_client.sh, demo.sh"
echo "- README.md"
echo ""
echo "To run:"
echo "1. ./compile.sh"
echo "2. ./run_server.sh (in one terminal)"
echo "3. ./run_client.sh (in another terminal)"
echo ""
echo "Or run automated demo: ./demo.sh"
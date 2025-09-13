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

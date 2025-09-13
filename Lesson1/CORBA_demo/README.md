# Java CORBA Implementation

**Reference:** [CORBA Implementation in Java](https://www.youtube.com/watch?v=8eEqik-0mCg)

## 1. Setup
```bash
    chmod +x setup.sh
    ./setup.sh
```

## 1. Initialize the Object Request Broker (ORB)
```bash
idlj -fall Addition.idl

orbd -ORBInitialPort 1050&

java StartServer -ORBInitialPort 1050 -ORBInitialHost localhost&
```

## 2. Running the client
```bash
chmod +x run_client.sh
./run_client.sh
```
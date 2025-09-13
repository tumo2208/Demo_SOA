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

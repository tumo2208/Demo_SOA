#!/bin/bash

# Set Java 8 environment
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Compile server
echo "Compiling server..."
javac AdditionService.java

# Run server
echo "Starting SOAP server..."
java AdditionService

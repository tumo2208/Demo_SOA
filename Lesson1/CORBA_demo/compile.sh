#!/bin/bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

echo "Compiling with java version $(javac --version$)"
javac *.java AdditionApp/*.java

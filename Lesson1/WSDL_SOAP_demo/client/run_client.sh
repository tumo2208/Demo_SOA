export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

WSDL_URL=http://localhost:8080/addition?wsdl
PACKAGE=com.example.client

echo "WSDL available!"

# Remove old generated stubs
rm -rf com/example/client/*

# Generate stubs
wsimport -keep -p $PACKAGE -d . $WSDL_URL

# Compile client
javac -d . com/example/client/*.java AdditionClient.java

# Run client
java -cp . com.example.client.AdditionClient

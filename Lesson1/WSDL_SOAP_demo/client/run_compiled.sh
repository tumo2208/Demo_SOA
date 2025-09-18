export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Run the compiled code included with its stubs
java -cp . com.example.client.AdditionClient

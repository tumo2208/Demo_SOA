import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import javax.xml.soap.*;
import javax.xml.namespace.QName;
import java.io.*;
import java.net.InetSocketAddress;

public class AdditionService {
    
    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/addition", new SOAPHandler());
        server.start();
        System.out.println("SOAP server running at http://localhost:" + port + "/addition");
    }
    
    static class SOAPHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // Read incoming SOAP message
                MessageFactory messageFactory = MessageFactory.newInstance();
                SOAPMessage requestMessage = messageFactory.createMessage(null, exchange.getRequestBody());
                
                System.out.println("Received SOAP Request:");
                requestMessage.writeTo(System.out);
                System.out.println("\n");
                
                // Parse SOAP Body
                SOAPBody requestBody = requestMessage.getSOAPBody();
                
                // Extract parameters using SOAP API
                SOAPElement addElement = (SOAPElement) requestBody.getFirstChild();
                
                int a = 0, b = 0;
                
                // Iterate through child elements to extract a and b
                java.util.Iterator<SOAPElement> iterator = addElement.getChildElements();
                while (iterator.hasNext()) {
                    SOAPElement element = iterator.next();
                    String localName = element.getLocalName();
                    String value = element.getTextContent().trim();
                    
                    if ("a".equals(localName)) {
                        a = Integer.parseInt(value);
                    } else if ("b".equals(localName)) {
                        b = Integer.parseInt(value);
                    }
                }
                
                System.out.println("Extracted values: a=" + a + ", b=" + b);
                
                // Calculate result
                int sum = a + b;
                System.out.println("Calculated sum: " + sum);
                
                // Create SOAP Response
                SOAPMessage responseMessage = messageFactory.createMessage();
                SOAPPart responsePart = responseMessage.getSOAPPart();
                SOAPEnvelope responseEnvelope = responsePart.getEnvelope();
                
                // Set namespace prefix
                responseEnvelope.removeNamespaceDeclaration(responseEnvelope.getPrefix());
                responseEnvelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
                responseEnvelope.setPrefix("soap");
                
                SOAPBody responseBody = responseEnvelope.getBody();
                responseBody.setPrefix("soap");
                
                // Add response element
                QName addResponseQName = new QName("http://example.com/soap", "addResponse");
                SOAPBodyElement addResponseElement = responseBody.addBodyElement(addResponseQName);
                
                // Add return element with result
                SOAPElement returnElement = addResponseElement.addChildElement("return");
                returnElement.addTextNode(String.valueOf(sum));
                
                // Save changes
                responseMessage.saveChanges();
                
                System.out.println("Sending SOAP Response:");
                responseMessage.writeTo(System.out);
                System.out.println("\n");
                
                // Write response to HTTP output stream
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                responseMessage.writeTo(outputStream);
                byte[] responseBytes = outputStream.toByteArray();
                
                // Send HTTP response
                exchange.getResponseHeaders().add("Content-Type", "text/xml; charset=utf-8");
                exchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(responseBytes);
                os.close();
                
            } catch (SOAPException e) {
                System.err.println("SOAP Exception:");
                e.printStackTrace();
                sendErrorResponse(exchange, "SOAP processing error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("General Exception:");
                e.printStackTrace();
                sendErrorResponse(exchange, "Server error: " + e.getMessage());
            }
        }
        
        private void sendErrorResponse(HttpExchange exchange, String errorMessage) throws IOException {
            byte[] response = errorMessage.getBytes();
            exchange.sendResponseHeaders(500, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }
}
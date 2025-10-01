import javax.xml.soap.*;
import javax.xml.namespace.QName;
import java.io.IOException;

public class AdditionClient {
    
    public static void main(String[] args) {
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            
            // Create SOAP Message
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();
            
            // Get SOAP Part and Envelope
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();
            
            // Remove default namespace prefix and set custom one
            envelope.removeNamespaceDeclaration(envelope.getPrefix());
            envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
            envelope.setPrefix("soap");
            
            // Get SOAP Header and Body
            SOAPHeader soapHeader = envelope.getHeader();
            SOAPBody soapBody = envelope.getBody();
            soapHeader.setPrefix("soap");
            soapBody.setPrefix("soap");
            
            // Add content to SOAP Body
            QName addQName = new QName("http://example.com/soap", "add", "ns2");
            SOAPBodyElement addElement = soapBody.addBodyElement(addQName);
            
            // Add parameters
            SOAPElement aElement = addElement.addChildElement("a");
            aElement.addTextNode("15");
            
            SOAPElement bElement = addElement.addChildElement("b");
            bElement.addTextNode("25");
            
            // Save and print the request
            soapMessage.saveChanges();
            System.out.println("SOAP Request:");
            soapMessage.writeTo(System.out);
            System.out.println("\n");
            
            // Send SOAP Message to SOAP Server
            String url = "http://localhost:8080/addition";
            System.out.println("Sending SOAP request to: " + url);
            SOAPMessage soapResponse = soapConnection.call(soapMessage, url);
            
            // Print the SOAP Response
            System.out.println("\nSOAP Response:");
            soapResponse.writeTo(System.out);
            System.out.println("\n");
            
            // Extract the result using SOAP API
            SOAPBody responseBody = soapResponse.getSOAPBody();
            
            // Check for SOAP Fault
            if (responseBody.hasFault()) {
                SOAPFault fault = responseBody.getFault();
                System.err.println("SOAP Fault: " + fault.getFaultString());
            } else {
                // Navigate to the return element
                // Method 1: Using QName
                QName returnQName = new QName("http://example.com/soap", "return");
                SOAPElement addResponse = (SOAPElement) responseBody.getFirstChild();
                
                // Iterate through child elements to find 'return'
                java.util.Iterator<SOAPElement> iterator = addResponse.getChildElements();
                while (iterator.hasNext()) {
                    SOAPElement element = iterator.next();
                    if (element.getLocalName().equals("return")) {
                        String result = element.getTextContent();
                        System.out.println("\nExtracted Result: " + result);
                        break;
                    }
                }
            }
            
            // Close connection
            soapConnection.close();
            
        } catch (SOAPException e) {
            System.err.println("SOAP Exception:");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IO Exception:");
            e.printStackTrace();
        }
    }
}
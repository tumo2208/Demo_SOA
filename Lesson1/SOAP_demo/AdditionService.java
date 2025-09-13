import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.ws.Endpoint;

@WebService(targetNamespace = "http://example.com/soap")
public class AdditionService {
    
    @WebMethod
    public int add(@WebParam(name = "a") int a, @WebParam(name = "b") int b) {
        return a + b;
    }
    
    public static void main(String[] args) {
        String url = "http://localhost:8080/addition";
        System.out.println("Publishing SOAP service at: " + url);
        Endpoint.publish(url, new AdditionService());
        System.out.println("Service published successfully!");
        System.out.println("WSDL available at: " + url + "?wsdl");
        
        System.out.println("Server is running. Press Ctrl+C to stop.");
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            System.out.println("Server stopped.");
        }
    }
}

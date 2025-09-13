import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.ws.Endpoint;

// Define the SOAP service
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
    }
}

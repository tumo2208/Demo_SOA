import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;

public class Client {
    public static void main(String[] args) {
        try {
            System.out.println("Looking up Calculator object...");
            String url = "rmi://localhost:1099/Calculator";
            Calculator calculator = (Calculator) Naming.lookup(url);
            
            System.out.println("Successfully connected to Calculator server!");
            
            int result1 = calculator.add(5, 3);
            System.out.println("Client: 5 + 3 = " + result1);
            
            int result2 = calculator.add(10, 20);
            System.out.println("Client: 10 + 20 = " + result2);
            
        } catch (Exception e) {
            System.err.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

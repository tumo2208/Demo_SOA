import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.net.MalformedURLException;

public class Server {
    public static void main(String[] args) {
        try {
            System.out.println("Starting RMI registry on port 1099...");
            LocateRegistry.createRegistry(1099);
            
            System.out.println("Creating Calculator object...");
            CalculatorImpl calculator = new CalculatorImpl();
            
            String name = "Calculator";
            System.out.println("Binding object to name: " + name);
            Naming.rebind("rmi://localhost:1099/" + name, calculator);
            
            System.out.println("RMI Server ready and waiting...");
            System.out.println("Press Ctrl+C to stop the server.");
            
            while (true) {
                Thread.sleep(1000);
            }
            
        } catch (Exception e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

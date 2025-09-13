import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

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
            // Read SOAP request from HTTP body
            InputStream is = exchange.getRequestBody();
            String requestXML = new BufferedReader(new InputStreamReader(is))
                    .lines().reduce("", (acc, line) -> acc + line);

            // Extract parameters manually
            int a = extractInt(requestXML, "<a>", "</a>");
            int b = extractInt(requestXML, "<b>", "</b>");
            int sum = a + b;

            // Read response template from file
            String templateXML = new String(Files.readAllBytes(Paths.get("soap_response.xml")));

            // Inject computed result
            String responseXML = templateXML.replace("{RESULT}", String.valueOf(sum));

            // Send HTTP response
            exchange.getResponseHeaders().add("Content-Type", "text/xml; charset=utf-8");
            exchange.sendResponseHeaders(200, responseXML.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseXML.getBytes());
            os.close();
        }

        private int extractInt(String xml, String startTag, String endTag) {
            int start = xml.indexOf(startTag) + startTag.length();
            int end = xml.indexOf(endTag);
            return Integer.parseInt(xml.substring(start, end).trim());
        }
    }
}

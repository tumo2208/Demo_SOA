public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    try {
        while (true) {
            System.out.print("\nDo you want to send a SOAP request? (yes/exit): ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            System.out.println("Reading SOAP request from file...");

            // Read SOAP request from file
            String soapRequest = readSOAPRequestFromFile("soap_request.xml");
            System.out.println("SOAP Request loaded:\n" + soapRequest);

            URL url = new URL("http://localhost:8080/addition");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request properties
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            connection.setRequestProperty("SOAPAction", "");
            connection.setDoOutput(true);

            System.out.println("\nSending SOAP request...");

            // Send request
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(soapRequest);
            writer.flush();
            writer.close();

            // Check response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response code: " + responseCode);

            // Read response
            BufferedReader reader;
            if (responseCode == 200) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line + "\n");
            }
            reader.close();

            System.out.println("\nSOAP Response:");
            System.out.println(response.toString());

            // Try to extract the result
            String responseStr = response.toString();
            if (responseStr.contains("<return>")) {
                int start = responseStr.indexOf("<return>") + 8;
                int end = responseStr.indexOf("</return>");
                if (end > start) {
                    String result = responseStr.substring(start, end);
                    System.out.println("\nExtracted Result: " + result);
                }
            }
        }
    } catch (FileNotFoundException e) {
        System.out.println("Error: soap_request.xml file not found!");
        System.out.println("Please create soap_request.xml file with your SOAP request.");
    } catch (Exception e) {
        System.out.println("Error calling SOAP service:");
        e.printStackTrace();
    } finally {
        scanner.close();
    }
}

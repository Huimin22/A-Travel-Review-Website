package server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.Socket;


public class HttpFetcher {
    public static int PORT = 80;

    /**
     * Sends an HTTP GET request to the specified host and path with resource, and retrieves the JSON response.
     *
     * @param host               The host name or IP address of the server.
     * @param pathAndResource    The path and resource on the server to fetch.
     * @return                   A JsonObject containing the JSON response from the server, or null if an error occurs.
     */
    public static JsonObject fetch(String host, String pathAndResource) {
        JsonObject jsonRes = null;
        StringBuffer buf = new StringBuffer();
        try {
            Socket socket = new Socket(host, PORT);
            OutputStream out = socket.getOutputStream();
            InputStream inStream = socket.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            String request = getRequest(host, pathAndResource);
            out.write(request.getBytes()); // send HTTP request to the server
            out.flush();

            String line = reader.readLine(); // read HTTP response from server
            while (line != null) {
                buf.append(line + System.lineSeparator());
                line = reader.readLine();
            }

            String allResponse = buf.toString();
            int begin = allResponse.indexOf("{");
            int end = allResponse.lastIndexOf("}", allResponse.lastIndexOf("}") - 1);

            if (begin != -1 && end != -1 && end > begin && allResponse != null) {
                String jsonStr = allResponse.substring(begin, end+2);
                jsonRes = JsonParser.parseString(jsonStr).getAsJsonObject();
            } else {
                return null;
            }

        } catch (IOException e) {
            System.out.println("HTTPFetcher:IOException occured during download: " + e.getMessage());
        }
        return jsonRes;
    }

    /**
     * Constructs and returns an HTTP GET request string with the specified host, path, and resource.
     *
     * @param host               The host name or IP address of the server.
     * @param pathResourceQuery  The path, resource, and query parameters for the HTTP request.
     * @return                   The formatted HTTP GET request string.
     */
    private static String getRequest(String host, String pathResourceQuery) {
        String request = "GET " + pathResourceQuery + " HTTP/1.1" + System.lineSeparator() // GET request
                + "Host: " + host + System.lineSeparator()
                + "Connection: close" + System.lineSeparator() // make sure the server closes the connection after we fetch one page
                + System.lineSeparator();
        return request;
    }

}

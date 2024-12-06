package uni.local;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import uni.local.RestHandler;

public class Server {
    private ServerSocket serverSocket;
    private final RestHandler restHandler = new RestHandler();

    public void start() {

        try {
            serverSocket = new ServerSocket(10001);
            System.out.println("Server started on port 10001...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     OutputStream out = clientSocket.getOutputStream()) {

                    String line;
                    StringBuilder request = new StringBuilder();
                    while (!(line = in.readLine()).isEmpty()) {
                        request.append(line).append("\n");
                    }
                    String requestBody = readRequestBody(in);
                    String response = restHandler.handleRequest(request.toString(), requestBody);
                    out.write(response.getBytes());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readRequestBody(BufferedReader in) throws IOException {
        StringBuilder body = new StringBuilder();
        while (in.ready()) {
            body.append((char) in.read());
        }
        return body.toString();
    }

    public void stop() {
        try {
            serverSocket.close();
            System.out.println("Server stopped.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}


package uni.local;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import uni.local.controllers.PostgresContainerManager;
import uni.local.controllers.UserController;

public class Server {
    private ServerSocket serverSocket;

    public void start() {
        // Start the PostgreSQL container
        PostgresContainerManager.startContainer();

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
                    String response = handleRequest(request.toString(), requestBody);
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

    private String handleRequest(String request, String requestBody) {
        UserController userController = new UserController();
        if (request.startsWith("POST /users")) {
            return userController.register(requestBody);
        } else if (request.startsWith("POST /sessions")) {
            return userController.login(requestBody);
        } else {
            return "HTTP/1.1 418 Not Found\r\n\r\n";
        }
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


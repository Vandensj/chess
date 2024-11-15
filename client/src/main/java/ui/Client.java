package ui;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;

public class Client {

    private WebSocket webSocket;
    private final ServerFacade serverFacade;
    private final String port;

    public Client(String serverPort) {
        port = serverPort;
        serverFacade = new ServerFacade(port);
    }

    public String getPort() {
        return port;
    }

    public void connectWebSocket(String webSocketUri) {
//        HttpClient client = HttpClient.newHttpClient();
//        this.webSocket = client.newWebSocketBuilder()
//                .buildAsync(URI.create(webSocketUri), new WebSocketListener())
//                .join();
    }

    public ServerFacade getServerFacade() {
        return serverFacade;
    }

    private static class WebSocketListener implements WebSocket.Listener {

        @Override
        public void onOpen(WebSocket webSocket) {
            System.out.println("Connected to WebSocket server.");
            WebSocket.Listener.super.onOpen(webSocket);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            System.err.println("WebSocket error: " + error.getMessage());
        }
    }

    // Method to send a message to the WebSocket
    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.sendText(message, true);
        }
    }
}

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

    public ServerFacade getServerFacade() {
        return serverFacade;
    }
}

package ui;

import chess.ChessGame;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {

    private String BASE_URL;

    ServerFacade(String port) {
        this.BASE_URL = "http://localhost:" + port + "/";
    }

    public String joinGame(String gameID, ChessGame.TeamColor playerColor, String authToken) throws Exception {
        String uri = BASE_URL + "game";
        String json = "{\"playerColor\":\"" + playerColor.toString() + "\",\"gameID\":\"" + gameID + "\"}";
        return sendGameRequest(uri, authToken, json);
    }

    public String createGame(String gameName, String authToken) throws Exception{
        String uri = BASE_URL + "game";
        String json = "{\"gameName\":\"" + gameName + "\"}";
        return sendGameRequest(uri, authToken, json);
    }

    public String listGames(String authToken) throws Exception {
        String uri = BASE_URL + "game";
        return sendGetRequest(uri, authToken);
    }

    public String logout(String authToken) throws Exception {
        String uri = BASE_URL + "session";
        return sendDeleteRequest(uri, authToken);
    }

    public String registerUser(String username, String password, String email) throws Exception {
        String uri = BASE_URL + "user";
        String json = "{\"username\":\"" + username + "\",\"password\":\"" + password +
                "\",\"email\":\"" + email + "\"}";
        return sendPostRequest(uri, json);
    }

    public String login(String username, String password) throws Exception {
        String url = this.BASE_URL + "session";
        String json = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        return sendPostRequest(url, json);
    }

    private String sendPostRequest(String url, String json) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private String sendGameRequest(String url, String authToken, String json) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private String sendGetRequest(String url, String authToken) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .uri(new URI(url))
                .header("Authorization", authToken)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private String sendDeleteRequest(String url, String authToken) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private String sendPutRequest(String url, String authToken, String json) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}

package server;

import chess.ChessGame;
import chess.ChessGameTypeAdapter;
import com.google.gson.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServerFacade {
    private final String serverUrl;
    private static String authToken;
    private record ListGamesResponse(List<GameData> games) {}

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clear() throws ResponseException {
        this.makeRequest("DELETE", "/db", null, null);
    }

    public AuthData register(UserData userData) throws ResponseException {
        try {
            AuthData authData = this.makeRequest("POST", "/user", userData, AuthData.class);
            authToken = authData.authToken();
            return authData;
        } catch (ResponseException e) {
            return null;
        }
    }

    public AuthData login(UserData userData) throws ResponseException {
        try {
            AuthData authData = this.makeRequest("POST", "/session", userData, AuthData.class);
            authToken = authData.authToken();
            return authData;
        } catch (ResponseException e) {
            return null;
        }
    }

    public void logout() throws ResponseException {
        this.makeRequest("DELETE", "/session", null, null);
    }

    public int create(String gameName) {
        try {
            record CreateGameRequest(String gameName) {}
            record CreateGameResponse(int gameID) {}
            CreateGameRequest request = new CreateGameRequest(gameName);
            var response = this.makeRequest("POST", "/game", request, CreateGameResponse.class);
            return response.gameID();
        } catch (ResponseException e) {
            return 0;
        }
    }

    public List<GameData> list() throws ResponseException {
        var response = this.makeRequest("GET", "/game", null, ListGamesResponse.class);
        return response.games();
    }

    public void join(String color, int gameID) throws ResponseException {
        record JoinGameRequest(String playerColor, int gameID) {}
        JoinGameRequest request = new JoinGameRequest(color, gameID);
        this.makeRequest("PUT", "/game", request, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            http.addRequestProperty("Authorization", authToken);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            if ((responseClass != null) && responseClass.equals(ListGamesResponse.class)) {
                return readGameBody(http);
            } else {
                return readBody(http, responseClass);
            }
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }
            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readGameBody(HttpURLConnection http) throws IOException {
        ListGamesResponse listResponse = new ListGamesResponse(new ArrayList<>());
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                String readerString = new BufferedReader(reader).lines().collect(Collectors.joining());
                JsonObject jsonObject = JsonParser.parseString(readerString).getAsJsonObject();
                JsonArray gameArray = jsonObject.get("games").getAsJsonArray();
                for (int i = 0; i < gameArray.size(); i++) {
                    JsonObject gameDataObj = gameArray.get(i).getAsJsonObject();
                    var gameID = gameDataObj.get("gameID").getAsInt();
                    String whiteUsername;
                    try {
                        whiteUsername = gameDataObj.get("whiteUsername").getAsString();
                    } catch (Exception e) {
                        whiteUsername = null;
                    }
                    String blackUsername;
                    try {
                        blackUsername = gameDataObj.get("blackUsername").getAsString();
                    } catch (Exception e) {
                        blackUsername = null;
                    }
                    var gameName = gameDataObj.get("gameName").getAsString();
                    JsonObject chessGameObj = gameDataObj.get("game").getAsJsonObject();
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(ChessGame.class, new ChessGameTypeAdapter());
                    Gson gson = gsonBuilder.create();
                    var game = gson.fromJson(chessGameObj, ChessGame.class);
                    GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                    listResponse.games().add(gameData);
                }
            }
        }
        @SuppressWarnings("unchecked") T response = (T) listResponse;
        return response;
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}

package tictactoe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.quarkus.arc.config.ConfigProperties;
import org.apache.http.HttpStatus;
import org.apache.maven.settings.Server;
import org.creditshelf.tictactoe.entity.Game;
import org.creditshelf.tictactoe.entity.Move;
import org.creditshelf.tictactoe.entity.Player;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.HttpMethod;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

abstract class AbstractCommonTest {

    private static HttpClient httpClient = HttpClient.newBuilder().build();
    private static Gson gson = new Gson();

    @ConfigProperty(name = "quarkus.http.test-port")
    private int testPort;

    Game joinGame(Long gameId, Player player) throws Exception {
        String json = gson.toJson(player);
        String Url = String.format("http://localhost:%s/game/join/%s",testPort, gameId);
        return doGameHttpRequest(HttpMethod.POST, json, Url);
    }

    Game createGame(Player player) throws Exception {
        String json = gson.toJson(player);
        String Url = String.format("http://localhost:%s/game/create",testPort);
        return doGameHttpRequest(HttpMethod.POST, json, Url);
    }

    Game getGame(Long gameId) throws Exception {
        String Url = String.format("http://localhost:%s/game/%s",testPort,gameId);
        return doGameHttpRequest(HttpMethod.GET, null, Url);
    }

    Game applyMove(Move move) throws Exception {
        String json = gson.toJson(move);
        String Url = String.format("http://localhost:%s/game/play",testPort);
        return doGameHttpRequest(HttpMethod.POST, json, Url);
    }

    Player createPlayer(Player player) throws Exception {
        String json = gson.toJson(player);
        String Url = String.format("http://localhost:%s/player/register",testPort);
        return doPlayerHttpRequest(HttpMethod.POST, json, Url);
    }

    Player getPlayer(Player player) throws Exception {
        String json = gson.toJson(player);
        String Url = String.format("http://localhost:%s/player/%s",testPort,player.getEmail());
        try{
            return doPlayerHttpRequest(HttpMethod.GET, json, Url);
        }catch(Exception e){
            return null;
        }
    }

    Player getOrCreatePlayer(Player player) throws Exception {
        Player result = getPlayer(player);
        if(result==null){
            result = createPlayer(player);
        }
        return result;
    }

    Player doPlayerHttpRequest(String httpMethod, String json, String Url) throws Exception {
        HttpResponse<String> response = getHttpResponse(httpMethod, Url, json);
        Player result = gson.fromJson(response.body().toString(),Player.class);
        return result;
    }

    Game doGameHttpRequest(String httpMethod, String json, String Url) throws Exception {
        HttpResponse<String> response = getHttpResponse(httpMethod, Url, json);
        Game result = gson.fromJson(response.body().toString(),Game.class);
        return result;
    }

    HttpResponse<String> getHttpResponse(String httpMethod, String Url, String json) throws Exception {
        HttpRequest request = getHttpRequest(httpMethod, Url, json);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode()!= HttpStatus.SC_OK){
            throw new Exception(String.format("Response Code %s , Message : %s", response.statusCode(), response.body().toString()));
        }
        return response;
    }

    protected HttpRequest getHttpRequest(String httpMethod, String url, String json){
        if(httpMethod.equals(HttpMethod.POST)){
            return HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .uri(URI.create(String.format(url)))
                    .header("Content-Type", "application/json")
                    .headers("Accept","application/json")
                    .build();
        }else{
            return HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(String.format(url)))
                    .header("Content-Type", "application/json")
                    .headers("Accept","application/json")
                    .build();
        }
    }
}
package javahttp.libs;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import javahttp.helpers.Json;

import java.io.IOException;

public class Res {
    private HttpExchange exchange;
    private Boolean isClosed;

    public Res addHeader(String key, String value) {
        if (isClosed) {
            System.err.println("Trying to add header after close");
            return this;
        }
        exchange.getResponseHeaders().add(key, value);
        return this;
    }

    public void sendSilent(int rCode, String response) {
        try {
            send(rCode, response);
        } catch (IOException e) {
            exchange.close();
            e.printStackTrace();
        }
    }

    public void sendSilent(int rCode, Object response) {
        Gson gson = Json.getGson();
        sendSilent(rCode, gson.toJson(response));
    }

    public void send(int rCode, String response) throws IOException {
        if (isClosed) {
            System.err.println("Trying to set header after close");
            return;
        }
        exchange.sendResponseHeaders(rCode, response.length());
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }

    public void send(int rCode, Object response) throws IOException {
        Gson gson = Json.getGson();
        send(rCode, gson.toJson(response));
    }

    public void close() {
        exchange.close();
        isClosed = true;
    }

    public Res(HttpExchange exchange) {
        isClosed = false;
        this.exchange = exchange;
    }
}

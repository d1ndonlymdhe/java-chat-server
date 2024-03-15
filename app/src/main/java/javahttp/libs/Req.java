package javahttp.libs;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import javahttp.helpers.Json;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Req {
    private URI rawUri;
    public String path;
    public String method;
    public String body;


    private HttpExchange exchange;
    public Map<String, String> query;

    public Req(HttpExchange exchange) {
        this.exchange = exchange;
        this.rawUri = exchange.getRequestURI();
        this.path = rawUri.getPath();
        this.method = exchange.getRequestMethod();
        InputStream bodyStream = exchange.getRequestBody();

        char c;
        StringBuilder s = new StringBuilder();
        try {
            while (bodyStream.available() > 0) {
                c = (char) bodyStream.read();
                s.append(c);
            }
        } catch (IOException e) {
            System.out.println("Error Reading Body");
        }

        this.body = s.toString();
        String q = rawUri.getQuery();

        this.query = new HashMap<>();

        if (q != null && !q.isEmpty()) {
            String[] queryParts = q.split("&");
            for (String qq : queryParts) {
                String[] eqSplit = q.split("=", 2);
                if (eqSplit.length == 2) {
                    query.put(eqSplit[0], eqSplit[1]);
                }
            }
        }

    }

    public <T> T json(Class<T> ClassName) {
        Gson gson = Json.getGson();
        return gson.fromJson(body, ClassName);
    }

}

package javahttp;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import javahttp.libs.Path;
import javahttp.libs.Req;
import javahttp.libs.Res;
import javahttp.libs.Route;
import javahttp.routes.*;

import java.io.IOException;
import java.net.URI;
import java.util.*;


public class Server {
    static List<Class<? extends Route>> routes;
    static Map<String, Route> pathMap;

    public static void main(String[] args) throws Exception {
        routes = new ArrayList<>(List.of(
                Index.class,
                Signup.class,
                Login.class,
                Search.class
                ));
        pathMap = new HashMap<>();
        for (Class<? extends Route> r : routes) {
            Path data = r.getAnnotation(Path.class);
            if (data == null) {
                System.out.println("No path annotation in class " + r.getName());
            } else {
                System.out.println(data.value());
                pathMap.put(data.value(), r.getConstructor().newInstance());
            }

        }
        HttpServer server = HttpServer.create(new java.net.InetSocketAddress(8080), 100);
        // Create a context for handling requests
        server.createContext("/", new MyHandler());
        // Set the executor
        server.setExecutor(null);
        // Start the server
        server.start();
        System.out.println("Server is running on port 8080");
    }

    // Define a handler for incoming HTTP requests
    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            URI reqUri = exchange.getRequestURI();
            String reqPath = reqUri.getPath();
            String requestMethod = exchange.getRequestMethod();
            Headers resHeaders = exchange.getResponseHeaders();

            resHeaders.add("Access-Control-Allow-Methods", "GET, OPTIONS, POST, PUT, DELETE");
            resHeaders.add("Access-Control-Allow-Headers", "Content-Type,Authorization");
            resHeaders.add("Access-Control-Allow-Origin", "*");
            System.out.println("Hello");

            if (requestMethod.equals("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }
            if (pathMap.containsKey(reqPath)) {
                pathMap.get(reqPath).Handler(new Req(exchange), new Res(exchange));
            }

        }
    }
}

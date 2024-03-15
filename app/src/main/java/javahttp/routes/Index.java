package javahttp.routes;

import javahttp.libs.Path;
import javahttp.libs.Req;
import javahttp.libs.Res;
import javahttp.libs.Route;

import java.io.IOException;


@Path("/")
public class Index implements Route {
    @Override
    public void Handler(Req req, Res res) {
        String response = "<h1>Testing</h1>";
        try {
            res.addHeader("Content-Type", "text/html").send(200, response);
        } catch (IOException error) {
            System.err.println("Error writing data");
        }
    }
}

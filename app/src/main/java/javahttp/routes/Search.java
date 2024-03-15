package javahttp.routes;

import javahttp.helpers.Json;
import javahttp.libs.Path;
import javahttp.libs.Req;
import javahttp.libs.Res;
import javahttp.libs.Route;
import javahttp.models.User;
import javahttp.routes.exception.InternalErrorException;
import javahttp.routes.exception.InvalidRequestException;

import java.util.List;


class SearchResult {
    Boolean success;
    List<User> results;
}

@Path("/search")
public class Search implements Route {

    @Override
    public void Handler(Req req, Res res) {
        try {
            if (req.query.containsKey("username")) {
                String username = req.query.get("username");
                if (username == null || username.isEmpty()) {
                    throw new InvalidRequestException();
                }
                try {
                    List<User> results = User.searchUser(username);
                    SearchResult searchResult = new SearchResult();
                    searchResult.success = true;
                    searchResult.results = results;
                    System.out.println(Json.getGson().toJson(searchResult));
                    res.sendSilent(200, searchResult);
                } catch (Exception error) {
                    throw new InternalErrorException();
                }
            }
        } catch (InvalidRequestException err) {
            res.sendSilent(400, err.getMessage());
        } catch (InternalErrorException err) {
            res.sendSilent(500, err.getMessage());
        }
    }
}

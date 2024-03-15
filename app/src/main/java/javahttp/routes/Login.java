package javahttp.routes;

import javahttp.libs.Path;
import javahttp.libs.Req;
import javahttp.libs.Res;
import javahttp.libs.Route;
import javahttp.models.User;
import javahttp.routes.exception.InternalErrorException;
import javahttp.routes.exception.InvalidRequestException;


class loginReq {
    String username;
    String password;
}


@Path("/login")
public class Login implements Route {
    @Override
    public void Handler(Req req, Res res) {
        try {

            loginReq payload = req.json(loginReq.class);
            if (payload == null) {
                throw new InvalidRequestException();
            }
            String username = payload.username;
            String password = payload.password;
            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                throw new InvalidRequestException();
            }
            try {
                if (User.checkPassword(username, password)) {
                    res.sendSilent(200, "Login Successful");
                } else {
                    res.sendSilent(401, "Invalid Credentials");
                }
            } catch (Exception error) {
                throw new InternalErrorException();
            }
        } catch (InvalidRequestException err) {
            res.sendSilent(400, err.getMessage());
        } catch (InternalErrorException err) {
            res.sendSilent(500, err.getMessage());
        }
    }
}

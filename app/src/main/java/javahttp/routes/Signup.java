package javahttp.routes;


import javahttp.helpers.Hash;
import javahttp.libs.Path;
import javahttp.libs.Req;
import javahttp.libs.Res;
import javahttp.libs.Route;


import javahttp.models.User;
import javahttp.routes.exception.InternalErrorException;
import javahttp.routes.exception.InvalidRequestException;
import javahttp.routes.exception.UserExistsException;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.time.Instant;
import java.util.UUID;

class signupReq {
    String username;
    String password;
}


@Path("/signup")
public class Signup implements Route {
    @Override
    public void Handler(Req req, Res res) {
        try {
            signupReq payload = req.json(signupReq.class);
            if (payload == null) {
                throw new InvalidRequestException();
            }
            String username = payload.username;
            String password = payload.password;
            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                throw new InvalidRequestException();
            }
            try {
                if (User.checkUserExists(username)) {
                    throw new UserExistsException();
                }



                if (User.addUser(username, password)) {
                    res.sendSilent(200, "User Created");
                } else {
                    throw new InternalErrorException();
                }
            } catch (SQLException err) {
                throw new InternalErrorException();
            }
        } catch (InvalidRequestException | UserExistsException err) {
            sendError(res, err.getMessage());
        } catch (InternalErrorException err) {
            res.sendSilent(500, err.getMessage());
        }

    }

    void sendError(Res res, String msg) {
        try {
            res.send(400, msg);
        } catch (IOException err) {
            res.close();
        }
    }

}

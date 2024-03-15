package javahttp.routes;


import com.google.gson.JsonSyntaxException;
import javahttp.libs.*;
import javahttp.models.Notification;
import javahttp.models.Token;
import javahttp.routes.exception.InternalErrorException;
import javahttp.routes.exception.InvalidRequestException;

import java.sql.SQLException;
import java.util.Objects;
import java.util.List;


class NotificationReq {
    String token;
}


@Path("/getNotifications")
public class GetNotifications implements Route {
    @Override
    public void Handler(Req req, Res res) {
        try {
            if (!Objects.equals(req.method, "POST")) {
                throw new InvalidRequestException("Method Not Allowed");
            }
            try {
                NotificationReq payload = req.json(NotificationReq.class);

                if (payload == null) {
                    throw new InvalidRequestException();
                }
                String token = payload.token;
                if (token == null || token.isEmpty()) {
                    throw new InvalidRequestException();
                }

                Token dbToken = Token.getToken(token);
                if (dbToken == null) {
                    throw new InvalidRequestException("Invalid Token");
                }

                List<Notification> notifications = Notification.getNotifications(dbToken.userId);
                Response<List<Notification>> response = new Response<>(true, notifications);
                res.sendSilent(200, response);
            } catch (JsonSyntaxException e) {
                throw new InvalidRequestException();
            } catch (SQLException e) {
                throw new InternalErrorException();
            }
        } catch (InvalidRequestException err) {
            res.sendSilent(400, err.getMessage());
        } catch (InternalErrorException err) {
            res.sendSilent(500, err.getMessage());
        }
    }
}

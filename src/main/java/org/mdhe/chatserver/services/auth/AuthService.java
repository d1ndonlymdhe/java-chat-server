package org.mdhe.chatserver.services.auth;

import org.mdhe.chatserver.models.Token;
import org.mdhe.chatserver.models.User;
import org.mdhe.chatserver.repositories.TokenRepository;
import org.mdhe.chatserver.repositories.UserRepository;
import org.mdhe.chatserver.services.auth.types.SignupReq;
import org.mdhe.chatserver.utilsx.Hash;
import org.mdhe.chatserver.utilsx.Response;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class AuthService {

    final
    UserRepository userRepository;
    final
    TokenRepository tokenRepository;

    public AuthService(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }


    public Response<String> signup(SignupReq request) {
        if (request == null || request.username() == null || request.password() == null || request.username().isEmpty() || request.password().isEmpty())
            return new Response<>(false, "Invalid request");

        User u = userRepository.findByUsername(request.username());
        if (u != null)
            return new Response<>(false, "Username already exists");

        String username = request.username();
        String password = request.password();

        String salt = Hash.sha256(username + Instant.now().toEpochMilli());
        String hash = Hash.sha256(password + salt);

        userRepository.save(new User(username, hash, salt));
        return new Response<>(true, "Signup Successfully");
    }

    public Response<String> login(SignupReq request) {
        if (request == null || request.username() == null || request.password() == null || request.username().isEmpty() || request.password().isEmpty())
            return new Response<>(false, "Invalid request");

        User u = userRepository.findByUsername(request.username());
        if (u == null)
            return new Response<>(false, "Username does not exist");

        String password = request.password();
        String inputHash = Hash.sha256(password + u.getSalt());

        if (!u.getHash().equals(inputHash))
            return new Response<>(false, "Invalid password");

        Token newToken = new Token(u);
        tokenRepository.save(newToken);
        return new Response<>(true, newToken.getId());
    }

    public Optional<Token> validateToken(String token) {
        if (token == null) {
            return Optional.empty();
        }
        return tokenRepository.findById(token);
    }
}

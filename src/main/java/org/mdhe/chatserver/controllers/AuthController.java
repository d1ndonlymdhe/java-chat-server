package org.mdhe.chatserver.controllers;

import org.mdhe.chatserver.models.Token;
import org.mdhe.chatserver.repositories.TokenRepository;
import org.mdhe.chatserver.models.User;
import org.mdhe.chatserver.repositories.UserRepository;
import org.mdhe.chatserver.services.auth.AuthService;
import org.mdhe.chatserver.services.auth.types.SignupReq;
import org.mdhe.chatserver.utilsx.Hash;
import org.mdhe.chatserver.utilsx.Response;
import org.springframework.web.bind.annotation.*;


record signupRequest(String username, String password) {
}

@RestController
public class AuthController {


    final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/api/signup")
    @ResponseBody
    public Response<String> signup(@RequestBody SignupReq request) {
        return authService.signup(request);
    }

    @PostMapping("/api/login")
    @ResponseBody
    public Response<String>
    login(@RequestBody SignupReq request) {
        System.out.println("login request");
        return authService.login(request);
    }

}

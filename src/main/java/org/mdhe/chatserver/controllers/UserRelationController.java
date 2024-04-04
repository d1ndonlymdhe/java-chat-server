package org.mdhe.chatserver.controllers;


import jakarta.transaction.Transactional;
import org.mdhe.chatserver.models.Token;
import org.mdhe.chatserver.models.User;
import org.mdhe.chatserver.repositories.TokenRepository;
import org.mdhe.chatserver.repositories.UserRepository;
import org.mdhe.chatserver.services.auth.AuthService;
import org.mdhe.chatserver.services.friendship.FriendshipService;
import org.mdhe.chatserver.services.friendship.types.FriendshipStatus;
import org.mdhe.chatserver.utilsx.Response;
import org.mdhe.chatserver.utilsx.TokenedRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@RestController
public class UserRelationController {
    private final FriendshipService friendshipService;
    public UserRelationController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @PostMapping("/api/relation")
    @Transactional
    public Response<FriendshipStatus> relation(@RequestBody TokenedRequest<String> request) {
        return friendshipService.relation(request);
    }
}

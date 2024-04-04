package org.mdhe.chatserver.controllers;

import jakarta.transaction.Transactional;
import org.mdhe.chatserver.services.friendship.FriendshipService;
import org.mdhe.chatserver.services.friendship.types.FriendshipStatus;
import org.mdhe.chatserver.utilsx.Response;
import org.mdhe.chatserver.utilsx.TokenedRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class FriendshipController {

    private final FriendshipService friendshipService;


    @Autowired
    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }


    @PostMapping("/api/friendship/send")
    @ResponseBody
    @Transactional
    public Response<String> sendFriendRequest(@RequestBody TokenedRequest<String> request) {
        return friendshipService.sendFriendRequest(request);
    }

    @PostMapping("/api/friendship/accept")
    @ResponseBody
    @Transactional
    public Response<String> acceptFriendship(@RequestBody TokenedRequest<String> request) {
        return friendshipService.acceptFriendship(request);
    }

    @PostMapping("/api/friendship/reject")
    @ResponseBody
    @Transactional
    public Response<String> rejectFriendship(@RequestBody TokenedRequest<String> request) {
        return friendshipService.rejectFriendship(request);
    }


    @PostMapping("/api/friendship/delete")
    @ResponseBody
    @Transactional
    public Response<String> deleteFriendship(@RequestBody TokenedRequest<String> request) {
        return friendshipService.deleteFriendship(request);

    }

    @GetMapping("/api/friendship")
    @ResponseBody
    @Transactional
    public Response<FriendshipStatus> friendship(@RequestBody TokenedRequest<String> request) {
        return friendshipService.relation(request);
    }

}


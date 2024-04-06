package org.mdhe.chatserver.services.friendship;

import jakarta.transaction.Transactional;
import org.mdhe.chatserver.models.FriendRequest;
import org.mdhe.chatserver.models.Friendship;
import org.mdhe.chatserver.models.Token;
import org.mdhe.chatserver.models.User;
import org.mdhe.chatserver.repositories.FriendRequestRepository;
import org.mdhe.chatserver.repositories.FriendshipRepository;
import org.mdhe.chatserver.repositories.UserRepository;
import org.mdhe.chatserver.services.auth.AuthService;
import org.mdhe.chatserver.services.friendship.types.FriendshipStatus;
import org.mdhe.chatserver.utilsx.Response;
import org.mdhe.chatserver.utilsx.TokenedRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class FriendshipService {
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;

    private final AuthService authService;

    public FriendshipService(AuthService authService, UserRepository userRepository, FriendRequestRepository friendRequestRepository, FriendshipRepository friendshipRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.friendshipRepository = friendshipRepository;
    }

    public Response<String> sendFriendRequest(@RequestBody TokenedRequest<String> request) {
        if (request == null || request.data == null || request.token == null || request.token.isEmpty() || request.data.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Request");


        Optional<Token> t = authService.validateToken(request.token);

        if (t.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Token");

        User u = t.get().getUser();

        if (u.getId().equals(request.data))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't send a friend request to yourself");

        String friendId = request.data;

        if (u.getFriendships().stream().anyMatch(friendship -> friendship.getMember().getId().equals(friendId)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already Friends");


        if (u.getSentFriendRequests().stream().anyMatch(friendRequest -> friendRequest.getReceiver().getId().equals(friendId)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already Sent");


        List<FriendRequest> matchingRequest = u.getReceivedFriendRequests().stream().filter(friendRequest -> friendRequest.getSender().getId().equals(friendId)).toList();
        if (!matchingRequest.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pending Request");
        }

        Optional<User> friend = userRepository.findById(friendId);
        if (friend.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");

        FriendRequest fr = new FriendRequest(u, friend.get());
        System.out.println("self id == " + u.getId());
        System.out.println("fr id == " + fr.getId());

        friendRequestRepository.save(fr);
        return new Response<>(true, "Friend request sent");


    }

    public Response<String> acceptFriendship(@RequestBody TokenedRequest<String> request) {
        if (request == null || request.data == null || request.token == null || request.token.isEmpty() || request.data.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Request");

        Optional<Token> t = authService.validateToken(request.token);

        if (t.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Token");

        User u = t.get().getUser();

        if (u.getId().equals(request.data))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't send a friend request to yourself");

        String friendId = request.data;
        List<FriendRequest> matchingRequest = u.getReceivedFriendRequests().stream().filter(friendRequest -> friendRequest.getSender().getId().equals(friendId)).toList();
        if (matchingRequest.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No friend request received from this user");

        friendRequestRepository.delete(matchingRequest.getFirst());

        Optional<User> friend = userRepository.findById(request.data);
        if (friend.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");

        friendshipRepository.save(new Friendship(u, friend.get()));
        friendshipRepository.save(new Friendship(friend.get(),u));
        //TODO: set notifications
        return new Response<>(true, "Friend request accepted");
    }

    public Response<String> rejectFriendship(@RequestBody TokenedRequest<String> request) {
        if (request == null || request.data == null || request.token == null || request.token.isEmpty() || request.data.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Request");

        Optional<Token> t = authService.validateToken(request.token);

        if (t.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Token");

        String friendId = request.data;
        User u = t.get().getUser();

        if (u.getId().equals(friendId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot send reject own request");

        List<FriendRequest> req = u.getReceivedFriendRequests().stream().filter(r -> r.getSender().getId().equals(friendId)).limit(1).toList();

        if (req.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request Not Found");

        FriendRequest r = req.getFirst();
        friendRequestRepository.delete(r);
        return new Response<>(true, "Rejected Request");
    }

    public Response<String> deleteFriendship(@RequestBody TokenedRequest<String> request) {
        if (request == null || request.data == null || request.token == null || request.token.isEmpty() || request.data.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Request");

        Optional<Token> t = authService.validateToken(request.token);

        if (t.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Token");

        String friendId = request.data;
        User u = t.get().getUser();

        if (u.getId().equals(friendId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete self");

        List<Friendship> matchingFriendship1 = u.getFriendships().stream().filter(f -> f.getMember().getId().equals(friendId)).limit(1).toList();
        List<Friendship> matchingFriendship2 = userRepository.findById(friendId).get().getFriendships().stream().filter(f->f.getMember().getId().equals(u.getId())).limit(1).toList();

        if (matchingFriendship1.isEmpty() || matchingFriendship2.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friendship does not exist");
        friendshipRepository.delete(matchingFriendship1.getFirst());
        friendshipRepository.delete(matchingFriendship2.getFirst());
        return new Response<>(true, "Deleted Friendship");

    }

    
    public Response<FriendshipStatus> relation(TokenedRequest<String> request) {
        if (request == null || request.token == null || request.data == null)
            return new Response<>(false, FriendshipStatus.NONE);
        String friendId = request.data;
        Optional<Token> token = authService.validateToken(request.token);
        if (token.isPresent()) {
            User user = token.get().getUser();
            if (user.getFriendships().stream().anyMatch(f -> f.getMember().getId().equals(friendId))) {
                return new Response<>(true, FriendshipStatus.FRIENDS);
            }
            if (user.getSentFriendRequests().stream().anyMatch(s -> s.getReceiver().getId().equals(friendId))) {
                return new Response<>(true, FriendshipStatus.SENT);
            }
            if (user.getReceivedFriendRequests().stream().anyMatch(r -> r.getSender().getId().equals(friendId))) {
                return new Response<>(true, FriendshipStatus.RECEIVED);
            }
            return new Response<>(true, FriendshipStatus.NONE);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Token");
    }

    public Response<String> cancelRequest(TokenedRequest<String> request) {
        if (request == null || request.data == null || request.token == null || request.token.isEmpty() || request.data.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Request");

        Optional<Token> t = authService.validateToken(request.token);

        if (t.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Token");

        String friendId = request.data;
        User u = t.get().getUser();

        Optional<FriendRequest> sentRequest = u.getSentFriendRequests().stream().filter(f -> f.getReceiver().getId().equals(friendId)).limit(1).findFirst();
        if (sentRequest.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Sent Request");
        }

        friendRequestRepository.delete(sentRequest.get());
        return new Response<>(true, "Deleted Request");
    }


}

package org.mdhe.chatserver.controllers.web;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.mdhe.chatserver.models.Token;
import org.mdhe.chatserver.models.User;
import org.mdhe.chatserver.repositories.UserRepository;
import org.mdhe.chatserver.services.auth.AuthService;
import org.mdhe.chatserver.services.auth.types.SignupReq;
import org.mdhe.chatserver.services.friendship.FriendshipService;
import org.mdhe.chatserver.services.friendship.types.FriendshipStatus;
import org.mdhe.chatserver.services.search.SearchService;
import org.mdhe.chatserver.services.search.types.SearchResult;
import org.mdhe.chatserver.utilsx.Response;
import org.mdhe.chatserver.utilsx.TokenedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
public class Web {


    private final AuthService authService;
    private final FriendshipService friendshipService;

    private final UserRepository userRepository;

    private final SearchService searchService;

    public Web(AuthService authService, FriendshipService friendshipService, UserRepository userRepository, SearchService searchService) {
        this.authService = authService;
        this.friendshipService = friendshipService;
        this.userRepository = userRepository;
        this.searchService = searchService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("error", "");
        model.addAttribute("user", new SignupReq("", ""));
        return "login";
    }

    @PostMapping("/login")
    public String loginHandler(@ModelAttribute("user") SignupReq req, Model model, HttpServletResponse response) {
        Response<String> res = authService.login(req);
        if (res.success) {
            Cookie newCookie = new Cookie("token", res.data);
            newCookie.setPath("/");
            //30 days
            newCookie.setMaxAge(43819200);
            response.addCookie(newCookie);
            return "redirect:/";
        }
        model.addAttribute("user", req);
        model.addAttribute("error", res.data);
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("error", "");
        model.addAttribute("user", new SignupReq("", ""));
        return "signup";
    }

    @PostMapping("/signup")
    public String signupHandler(@ModelAttribute("user") SignupReq req, Model model) {
        Response<String> res = authService.signup(req);
        if (res.success)
            return "redirect:/login";
        model.addAttribute("user", req);
        model.addAttribute("error", res.data);
        return "signup";
    }

    @GetMapping("/search")
    public String search(Model model) {
        model.addAttribute("error", "");
        model.addAttribute("username", "");
        model.addAttribute("result", new ArrayList<SearchResult>());
        return "search";
    }

    @PostMapping("/search")
    @Transactional
    public String searchHandler(Model model, @ModelAttribute(name = "username") String username) {
        Response<List<SearchResult>> res = searchService.search(username);
        if (res.success) {
            model.addAttribute("error", "");
            model.addAttribute("username", username);
            model.addAttribute("result", res.data);
        } else {
            model.addAttribute("error", "");
            model.addAttribute("username", "");
            model.addAttribute("result", new ArrayList<SearchResult>());
        }
        return "search";
    }

    @GetMapping("/profile/{username}")
//    @Transactional
    public String profile(Model model, @PathVariable String username, @CookieValue(name = "token", required = false) Optional<String> token) {
        boolean isAuthorized = false;
        FriendshipStatus relation = FriendshipStatus.NONE;
        if (token.isPresent()) {
            Optional<Token> optionalToken = authService.validateToken(token.get());
            if (optionalToken.isPresent()) {
                Token t = optionalToken.get();
                User user = t.getUser();
                User friend = userRepository.findByUsername(username);

                Response<FriendshipStatus> statusResponse = friendshipService.relation(new TokenedRequest<>(token.get(), friend.getId()));
                relation = statusResponse.data;
                isAuthorized = true;
            }
        }
        model.addAttribute("message", "");
        model.addAttribute("error", "");
        model.addAttribute("username", username);
        model.addAttribute("authorized", isAuthorized);
        model.addAttribute("friendship", relation);
        return "profile";
    }

    @PostMapping("/profile/{username}")
//    @Transactional()
    public String handleRequest(Model model, @PathVariable String username, @CookieValue(name = "token", required = false) Optional<String> token, @ModelAttribute(name = "action") String action) {
        boolean isAuthorized = false;
        FriendshipStatus relation = FriendshipStatus.NONE;
        String error = "";
        String message = "";
        if (token.isPresent()) {
            Optional<Token> optionalToken = authService.validateToken(token.get());
            if (optionalToken.isPresent()) {
                User friend = userRepository.findByUsername(username);

                isAuthorized = true;

                String friendId = friend.getId();
                try {
                    Response<String> res;
                    switch (action) {
                        case "send" -> {
                            relation = FriendshipStatus.SENT;
                            res = friendshipService.sendFriendRequest(new TokenedRequest<>(token.get(), friendId));
                        }
                        case "unfriend" ->
                                res = friendshipService.deleteFriendship(new TokenedRequest<>(token.get(), friendId));
                        case "accept" -> {
                            relation = FriendshipStatus.FRIENDS;
                            res = friendshipService.acceptFriendship(new TokenedRequest<>(token.get(), friendId));
                        }
                        case "reject" ->
                                res = friendshipService.rejectFriendship(new TokenedRequest<>(token.get(), friendId));
                        case "cancel" ->
                                res = friendshipService.cancelRequest(new TokenedRequest<>(token.get(), friendId));
                        default -> {
                            res = null;
                            Response<FriendshipStatus> statusResponse = friendshipService.relation(new TokenedRequest<>(token.get(), friend.getId()));
                            relation = statusResponse.data;
                        }
                    }
                    if (res != null && res.success) {
                        message = res.data;
                    }
                } catch (Exception e) {
                    error = e.getMessage();
                }

            }
        } else {
            error = "Invalid Token";
        }
        model.addAttribute("message", message);
        model.addAttribute("error", error);
        model.addAttribute("username", username);
        model.addAttribute("authorized", isAuthorized);
        model.addAttribute("friendship", relation);
        return "profile";
    }

    @GetMapping("/")
    public String home(@CookieValue(name = "token", required = false) Optional<String> token) {
        if (token.isPresent()) {
            Optional<Token> t = authService.validateToken(token.get());
            if (t.isPresent()) {
                return "home";
            }
        }
        return "redirect:/login";
    }


}

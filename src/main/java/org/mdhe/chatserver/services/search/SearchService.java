package org.mdhe.chatserver.services.search;

import org.mdhe.chatserver.repositories.UserRepository;
import org.mdhe.chatserver.services.search.types.SearchResult;
import org.mdhe.chatserver.utilsx.Response;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private final UserRepository userRepository;

    public SearchService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Response<List<SearchResult>> search(String query) {
        if (query == null || query.isEmpty())
            return new Response<>(false, List.of());
        List<SearchResult> results = userRepository.findByUsernameStartingWith(query).map(user -> new SearchResult(user.getUsername(), user.getId())).toList();
        return new Response<>(true, results);
    }
}

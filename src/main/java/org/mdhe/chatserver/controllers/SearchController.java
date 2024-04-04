package org.mdhe.chatserver.controllers;


import jakarta.transaction.Transactional;
import org.mdhe.chatserver.repositories.UserRepository;
import org.mdhe.chatserver.services.search.SearchService;
import org.mdhe.chatserver.services.search.types.SearchResult;
import org.mdhe.chatserver.utilsx.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {


    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/api/search")
    @Transactional
    public Response<List<SearchResult>> search(@RequestParam(name = "query") String query) {
        return searchService.search(query);
    }
}

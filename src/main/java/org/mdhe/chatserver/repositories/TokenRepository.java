package org.mdhe.chatserver.repositories;

import org.mdhe.chatserver.models.Token;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TokenRepository extends CrudRepository<Token, String> {
    Iterable<Token> findByUserId(String userId);
}

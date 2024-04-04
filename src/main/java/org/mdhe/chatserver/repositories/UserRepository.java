package org.mdhe.chatserver.repositories;

import org.mdhe.chatserver.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.stream.Stream;

public interface UserRepository extends CrudRepository<User, String> {
    User findByUsername(String username);

    Stream<User> findByUsernameContaining(String username);

    Stream<User> findByUsernameStartingWith(String username);
}

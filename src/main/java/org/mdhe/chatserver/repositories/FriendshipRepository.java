package org.mdhe.chatserver.repositories;

import org.mdhe.chatserver.models.Friendship;
import org.mdhe.chatserver.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FriendshipRepository extends CrudRepository<Friendship, String> {
    List<Friendship> findByFriendOf(User user);
}

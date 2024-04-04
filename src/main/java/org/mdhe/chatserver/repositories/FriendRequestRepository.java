package org.mdhe.chatserver.repositories;

import org.mdhe.chatserver.models.FriendRequest;
import org.mdhe.chatserver.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FriendRequestRepository extends CrudRepository<FriendRequest, String> {
    FriendRequest findBySenderAndReceiver(User sender, User receiver);

    Iterable<FriendRequest> findByReceiver(User receiver);

    Iterable<FriendRequest> findBySender(User sender);


}

package org.mdhe.chatserver.repositories;

import org.mdhe.chatserver.models.Notification;
import org.springframework.data.repository.CrudRepository;


public interface NotificationRepository extends CrudRepository<Notification, String> {
    Iterable<Notification> findByUserId(String userId);
}

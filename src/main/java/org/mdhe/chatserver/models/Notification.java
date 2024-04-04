package org.mdhe.chatserver.models;


import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;

@Entity
@Table(name = "notifications")
public class Notification implements Serializable {
    @Id
    @UuidGenerator
    private String id;

    private String message;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    protected Notification() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Notification(String message, User user) {
        this.message = message;
        this.user = user;
    }

}

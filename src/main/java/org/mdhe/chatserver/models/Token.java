package org.mdhe.chatserver.models;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;

@Entity
@Table(name = "tokens")
@Inheritance(strategy = InheritanceType.JOINED)

public class Token implements Serializable {
    @Id
    @UuidGenerator
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Token(User user) {
        this.user = user;
    }

    protected Token() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

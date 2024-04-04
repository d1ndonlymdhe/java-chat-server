package org.mdhe.chatserver.models;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "friendships")
public class Friendship implements Serializable {
    @Id
    @UuidGenerator
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User friendOf;

    @ManyToOne(fetch = FetchType.LAZY)
    private User member;


    public Friendship(User friendOf, User member) {
        this.friendOf = friendOf;
        this.member = member;
    }

    protected Friendship(){

    }
    public User getFriendOf() {
        return friendOf;
    }

    public void setFriendOf(User friendOf) {
        this.friendOf = friendOf;
    }

    public User getMember() {
        return member;
    }

    public void setMember(User member) {
        this.member = member;
    }
}

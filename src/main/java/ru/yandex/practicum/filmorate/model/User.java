package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


public class User {
    private long id;
    private String name;
    private String login;
    private LocalDate birthday;
    private String email;
    private Map<Long, FriendshipStatus> friendshipStatusMap = new HashMap<>(); // здесь хранятся ид друзей

    public User(long id, String name, String login, LocalDate birthday, String email) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.birthday = birthday;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<Long, FriendshipStatus> getFriendshipStatusMap() {
        return friendshipStatusMap;
    }

    public void setFriendshipStatusMap(Map<Long, FriendshipStatus> friendshipStatusMap) {
        this.friendshipStatusMap = friendshipStatusMap;
    }
}
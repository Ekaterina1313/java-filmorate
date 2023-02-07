package ru.yandex.practicum.filmorate.model;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Builder
public class User {
    private long id;
    private String name;
    private String login;
    private LocalDate birthday;
    private String email;
    private final Set<Long> listOfFriends = new HashSet<>();
    private final Map<Long, FriendshipStatus> friendshipStatusMap = new HashMap<>();
    public User(long id, String name, String login, LocalDate birthday, String email) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.birthday = birthday;
        this.email = email;
    }

}
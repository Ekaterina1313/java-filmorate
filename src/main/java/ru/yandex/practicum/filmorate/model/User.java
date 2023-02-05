package ru.yandex.practicum.filmorate.model;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class User {
    private String name;
    private LocalDate birthday;
    private long id;
    private String login;
    private String email;
    private final Set<Long> listOfFriends = new HashSet<>();
    private final Map<Long, FriendshipStatus> friendshipStatusMap = new HashMap<>();
}
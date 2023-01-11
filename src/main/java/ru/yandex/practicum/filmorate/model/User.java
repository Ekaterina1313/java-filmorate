package ru.yandex.practicum.filmorate.model;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private String name;
    private LocalDate birthday;
    private long id;
    private String login;
    private String email;
    final Set<Long> listOfFriends = new HashSet<>();
}
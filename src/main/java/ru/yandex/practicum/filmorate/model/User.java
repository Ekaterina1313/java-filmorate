package ru.yandex.practicum.filmorate.model;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private String name;
    private LocalDate birthday;
    private long id;
    private String login;
    private String email;
}
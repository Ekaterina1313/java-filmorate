package ru.yandex.practicum.filmorate.model;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private String name;
    private LocalDate birthday;
    private int id;
    private String login;
    private String email;
}

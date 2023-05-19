package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;


@Getter
@Setter
public class User {
    private long id;
    private String name;
    private String login;
    private LocalDate birthday;
    private String email;

    public User(long id, String name, String login, LocalDate birthday, String email) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.birthday = birthday;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
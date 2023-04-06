package ru.yandex.practicum.filmorate.model;

import lombok.NonNull;

import java.time.LocalDate;


public class User {
    private long id;
    private String name;
    private String login;
    @NonNull
    private LocalDate birthday;
    private String email;

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
}
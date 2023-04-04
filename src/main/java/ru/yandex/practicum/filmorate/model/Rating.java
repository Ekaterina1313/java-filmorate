package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rating {
    private int id;
    private String name;

    public Rating(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Rating(int id) {
        this.id = id;
    }
}

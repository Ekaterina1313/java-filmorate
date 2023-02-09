package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rating {
    private int id;
    private RatingMPA name;

    public Rating(int id, RatingMPA name) {
        this.id = id;
        this.name = name;
    }
}

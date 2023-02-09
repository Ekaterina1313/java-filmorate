package ru.yandex.practicum.filmorate.model;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Film {
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long id;
    private int duration;
    private Set<Long> likes = new HashSet<>();
    /*private Set<String> genres = new HashSet<>();
    RatingMPA rating;*/
    private int ratingId;
    private Set<Integer> genreId;

    public Film(long id, String name, String description, LocalDate releaseDate, int duration, int ratingId, Set<Integer> genreId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.ratingId = ratingId;
        this.genreId = genreId;
    }
}
package ru.yandex.practicum.filmorate.model;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
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
    private Rating mpa;
    private List<Genre> genres;

    public Film(long id, String name, String description, LocalDate releaseDate, int duration, Rating mpa, List<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
    }
}
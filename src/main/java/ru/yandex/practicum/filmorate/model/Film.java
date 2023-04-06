package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class Film {
    private String name;
    @NonNull
    private String description;
    @NonNull
    private LocalDate releaseDate;
    private long id;
    private int duration;
    private Rating mpa;
    private Set<Genre> genres;

    public Film(long id, String name, String description, LocalDate releaseDate, int duration, Rating mpa, Set<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
    }
}
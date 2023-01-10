package ru.yandex.practicum.filmorate.model;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long id;
    private int duration;
    final Set<Long> likes = new HashSet<>();
}
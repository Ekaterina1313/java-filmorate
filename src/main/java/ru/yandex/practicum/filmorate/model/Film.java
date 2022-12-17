package ru.yandex.practicum.filmorate.model;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Film {
    private  String name;
    private String description;
    private LocalDate releaseDate;
    private long id;
    private int duration;
}
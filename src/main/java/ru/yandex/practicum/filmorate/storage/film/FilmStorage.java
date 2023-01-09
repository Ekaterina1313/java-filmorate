package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    Map<Long, Film> getFilms();
    Film addFilm(Film film);
    Film updateFilm(Film film);
    boolean isContainFilm(Film film);
}

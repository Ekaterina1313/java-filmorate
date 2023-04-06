package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FilmStorage {

    Map<Long, Film> getFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    boolean isContainFilm(long id);

    Film getFilmById(long id);

    List<Film> getTheMostPopularFilms(int count);
}
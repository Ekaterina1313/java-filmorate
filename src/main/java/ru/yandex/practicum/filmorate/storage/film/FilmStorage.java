package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    Map<Long, Film> getFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    boolean isContainFilm(long id);

    Film getFilmById(long id);

    List<Rating> getListOfRating();

    Rating getRatingById(int id);

    List<Genre> getListOfGenre();

    Genre getGenreById(int id);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

}

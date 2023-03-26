package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component ("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage { //логикa хранения, обновления и поиска объектов.
    private final Map<Long, Film> films = new HashMap<>();
    private long id = 1;

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(id);
        id++;
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public boolean isContainFilm(long id) {
        return films.containsKey(id);
    }

    @Override
    public Film getFilmById(long id) {
        return films.get(id);
    }

    @Override
    public List<Rating> getListOfRating() {
        return null;
    }

    @Override
    public Rating getRatingById(int id) {
        return null;
    }

    @Override
    public List<Genre> getListOfGenre() {
        return null;
    }

    @Override
    public Genre getGenreById(int id) {
        return null;
    }

    @Override
    public void addLike(long filmId, long userId) {
    }

    @Override
    public void deleteLike(long filmId, long userId) {
    }
}
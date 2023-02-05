package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Component
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
 }
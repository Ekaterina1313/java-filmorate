package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.Constants.DESCENDING_ORDER;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addLike(long filmId, long userId) {
        filmStorage.getFilmById(filmId).getLikes().add(userId);
        return filmStorage.getFilmById(filmId);
    }

    public Film deleteLike(long filmId, long userId) {
        filmStorage.getFilms().get(filmId).getLikes().remove(userId);
        return filmStorage.getFilmById(filmId);
    }

    public List<Film> getTheMostPopularFilms(Integer count, String sort) {
        return new ArrayList<>(filmStorage.getFilms().values()).stream()
                .sorted((f1, f2) -> compare(f1, f2, sort))
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film f1, Film f2, String sort) {
        int result = f1.getLikes().size() - (f2.getLikes().size());
        if (sort.equals(DESCENDING_ORDER)) {
            result = -1 * result;
        }
        return result;
    }
}
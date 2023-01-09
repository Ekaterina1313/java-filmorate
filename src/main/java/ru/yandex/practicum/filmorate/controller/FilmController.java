package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FileDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping ({"/films"})
@Slf4j
public class FilmController {

   private final InMemoryFilmStorage filmStorage;

    @Autowired
    public FilmController(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.debug("Текущее количество фильмов: {}", filmStorage.getFilms().size());
        return new ArrayList<>(filmStorage.getFilms().values());
    }

    @PostMapping
    public Film add(@RequestBody Film film) {
        if (isValid(film)) {
            log.debug("Добавлен новый фильм: " + film.getName());
        }
        return filmStorage.addFilm(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (filmStorage.isContainFilm(film)) {
            if (isValid(film)) {
                log.debug("Обновлена информация о фильме с id {}", film.getId());
            }
        } else {
            log.debug("Фильм с id {} не существует.", film.getId());
            throw new FileDoesNotExistException("Фильм с указанным id не существует.");
        }
        return filmStorage.updateFilm(film);
    }

    private boolean isValid(Film film) {
        if ((film.getName() == null) || (film.getName().isBlank())) {
            log.debug("Пустое название фильма");
            throw new ValidationException("Поле с названием фильма не должно быть пустым.");
        } else if (film.getDescription().length() > 200) {
            log.debug("Превышен лимит символов для описания фильма {}, максимальное количество символов: 200, текущее: {}",
                    film.getName(), film.getDescription().length());
            throw new ValidationException("Превышен лимит символов для описания фильма. Максимальная длина описания — 200 символов");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Указана неверная дата релиза фильма {}", film.getName());
            throw new ValidationException("Дата релиза фильма не должна быть раньше 28 декабря 1895 года.");
        } else if (film.getDuration() <= 0) {
            log.debug("Некорректно указана продолжительнось фильма {}", film.getName());
            throw new ValidationException("Продолжительность фильма не может быть отрицательной или равной нулю.");
        } else {
            return true;
        }
    }
}
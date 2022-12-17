package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FileDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping ({"/films"})
public class FilmController {
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();
    private static long id = 1;

    @GetMapping
    public List<Film> getFilms() {
        log.debug("Текущее количество фильмов: {}", films.size());
        List<Film> listFilms = new ArrayList<>();
        listFilms.addAll(films.values());
        return listFilms;
    }

    public boolean isValid(Film film) {
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

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws ValidationException {
        if (isValid(film)) {
            film.setId(id);
            id++;
            log.debug("Добавлен новый фильм: " + film.getName());
            films.put(film.getId(), film);
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        if (films.containsKey(film.getId())) {
            if (isValid(film)) {
                films.put(film.getId(), film);
                log.debug("Обновлена информация о фильме с id {}", film.getId());
            }
        } else {
            log.debug("Фильм с id {} не существует.", film.getId());
            throw new FileDoesNotExistException("Фильм с указанным id не существует.");
        }
        return film;
    }
}


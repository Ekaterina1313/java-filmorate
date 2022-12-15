package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping ({"/films"})
public class FilmController {
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Set<Film> films = new HashSet<>();
    private static int id = 1;

    @GetMapping
    public Set<Film> getFilms() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films;
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws ValidationException {
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
            film.setId(id);
            films.add(film);
            id++;
            log.debug("Добавлен новый фильм: " + film.getName());
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        Film targetFilm = null;
        for (Film element : films) {
            if (film.getId() == element.getId()) {
                targetFilm = element;
            }
            if (targetFilm == null) {
                log.debug(" Фильм с id {} не существует", film.getId());
                throw new ValidationException("Фильм с указанным id не существует.");
            } else {
                films.remove(targetFilm);
                films.add(film);
                log.debug("Обновлена информация о фильме {}", film.getName());
            }
        }
        return film;
    }
}


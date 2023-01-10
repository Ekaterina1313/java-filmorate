package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DoesNotExistException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static ru.yandex.practicum.filmorate.Constants.*;

@RestController
@RequestMapping ({"/films"})
@Slf4j
public class FilmController {

   private final InMemoryFilmStorage filmStorage;
   private final FilmService filmService;
   private final UserStorage userStorage;


    @Autowired
    public FilmController(InMemoryFilmStorage filmStorage, FilmService filmService, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
        this.userStorage = userStorage;
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
        if (filmStorage.isContainFilm(film.getId())) {
            if (isValid(film)) {
                log.debug("Обновлена информация о фильме с id {}", film.getId());
            }
        } else {
            log.debug("Фильм с id {} не существует.", film.getId());
            throw new DoesNotExistException("Фильм с указанным id не существует.");
        }
        return filmStorage.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable long id) {
        if (filmStorage.isContainFilm(id)) {
            return filmStorage.getFilmById(id);
        } else {
            log.debug("Фильм с id {} не существует.", id);
            throw new DoesNotExistException("Фильм с указанным id не существует.");
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable long id, @PathVariable long userId) {
        isExist(id, userId);
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable long id, @PathVariable long userId) {
        isExist(id, userId);
        return filmService.deleteLike(id, userId);
    }

    private boolean isExist(long filmId, long userId) {
        if (!filmStorage.isContainFilm(filmId)) {
            log.debug("Фильм с id {} не существует.", filmId);
            throw new DoesNotExistException("Фильм с указанным id не существует.");
        }
        if (!userStorage.isContainId(userId)) {
            log.debug(" Пользователь с id {} не существует.", userId);
            throw new DoesNotExistException("Пользователь с указанным id не зарегистрирован.");
        }
        return true;
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam (value = "count", defaultValue = "10", required = false) Integer count,
                                      @RequestParam (value = "sort", defaultValue = DESCENDING_ORDER, required = false) String sort) {
        if(!(SORTS.contains(sort))) {
            throw new IncorrectParameterException("size");
        }
        if (count <= 0) {
            throw new IncorrectParameterException("size");
        }
       return filmService.getTheMostPopularFilms(count, sort);
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
            log.debug("Некорректно указана продолжительность фильма {}", film.getName());
            throw new ValidationException("Продолжительность фильма не может быть отрицательной или равной нулю.");
        } else {
            return true;
        }
    }
}
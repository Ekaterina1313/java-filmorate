package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DoesNotExistException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.Constants.DESCENDING_ORDER;
import static ru.yandex.practicum.filmorate.Constants.SORTS;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public List<Film> getFilms() {
        log.debug("Текущее количество фильмов: {}", filmStorage.getFilms().size());
        return new ArrayList<>(filmStorage.getFilms().values());
    }

    public Film addFilm(Film film) {
        if (isValid(film)) {
            log.debug("Добавлен новый фильм: " + film.getName());
        }
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
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

    public Film getFilm(long id) {
        if (filmStorage.isContainFilm(id)) {
            return filmStorage.getFilmById(id);
        } else {
            log.debug("Фильм с id {} не существует.", id);
            throw new DoesNotExistException("Фильм с указанным id не существует.");
        }
    }

    public Film addLike(long filmId, long userId) {
        isExist(filmId, userId);
        filmStorage.getFilmById(filmId).getLikes().add(userId);
        return filmStorage.getFilmById(filmId);
    }

    public Film deleteLike(long filmId, long userId) {
        isExist(filmId, userId);
        filmStorage.getFilms().get(filmId).getLikes().remove(userId);
        return filmStorage.getFilmById(filmId);
    }

    public List<Film> getTheMostPopularFilms(Integer count, String sort) {
        if(!(SORTS.contains(sort))) {
            throw new IncorrectParameterException("sort. Введите один из предложенных вариантов: asc или desc.");
        }
        if (count <= 0) {
            throw new IncorrectParameterException("count. Значение параметра запроса не должно быть меньше 1");
        }
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
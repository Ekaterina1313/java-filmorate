package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenresStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

import static ru.yandex.practicum.filmorate.Constants.FIRST_DATE;
import static ru.yandex.practicum.filmorate.Constants.SORTS;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikesStorage likesStorage;
    private final GenresStorage genresStorage;
    private final RatingsStorage ratingsStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, LikesStorage likesStorage,
                       GenresStorage genresStorage, RatingsStorage ratingsStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.likesStorage = likesStorage;
        this.genresStorage = genresStorage;
        this.ratingsStorage = ratingsStorage;
    }

    public List<Film> getFilms() {
        log.debug("Текущее количество фильмов: {}", filmStorage.getFilms().size());
        return new ArrayList<>(filmStorage.getFilms().values());
    }

    public Film addFilm(Film film) {
        if (isValid(film)) {
            log.debug("Добавлен новый фильм: {}", film.getName());
        }
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (filmStorage.isContainFilm(film.getId())) {
            if (isValid(film)) {
                log.debug("Обновлена информация о фильме с id {}", film.getId());
            }
        } else {
            throw new EntityNotFoundException("Фильм с указанным id не существует.");
        }
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(long id) {
        if (filmStorage.isContainFilm(id)) {
            log.debug("Выполнен запрос на получение фильма с id {}", id);
            return filmStorage.getFilmById(id);
        } else {
            throw new EntityNotFoundException("Фильм с указанным id не существует.");
        }
    }

    public void addLike(long filmId, long userId) {
        isExist(filmId, userId);
        log.debug("Пользователь поставил отметку 'Нравится' фильму с id {}", filmId);
        likesStorage.addLike(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        isExist(filmId, userId);
        log.debug("Пользователь убрал отметку 'Нравится' у фильма с id {}", filmId);
        likesStorage.deleteLike(filmId, userId);
    }


    public List<Film> getTheMostPopularFilms(Integer count, String sort) {
        if (!SORTS.contains(sort)) {
            throw new IncorrectParameterException("sort. Введите один из предложенных вариантов: asc или desc.");
        }
        if (count <= 0) {
            throw new IncorrectParameterException("count. Значение параметра запроса не должно быть меньше 1");
        }
        log.debug("Пользователь запросил список самых популярных фильмов.");
        return likesStorage.getTheMostPopularFilms(count);
    }

    private boolean isExist(long filmId, long userId) {
        if (!filmStorage.isContainFilm(filmId)) {
            throw new EntityNotFoundException("Фильм с указанным id не существует.");
        }
        if (!userStorage.isContainId(userId)) {
            throw new EntityNotFoundException("Пользователь с указанным id не зарегистрирован.");
        }
        return true;
    }

    private boolean isValid(Film film) {
        if ((film.getName() == null) || (film.getName().isBlank())) {
            throw new ValidationException("Поле с названием фильма не должно быть пустым.");
        } else if (film.getDescription() == null || film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не должно быть пустым или превышать лимит символов. Максимальная длина описания - 200 символов.");
        } else if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(FIRST_DATE)) {
            throw new ValidationException("Дата релиза фильма не должна быть пустой и должна быть не раньше 28 декабря 1895 года.");
        } else if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма не может быть отрицательной или равной нулю.");
        } else {
            return true;
        }
    }

    public List<Rating> getListOfRating() {
        return ratingsStorage.getListOfRating();
    }

    public Rating getRatingById(int id) {
        return ratingsStorage.getRatingById(id);
    }

    public List<Genre> getListOfGenre() {
        return genresStorage.getListOfGenre();
    }

    public Genre getGenreById(long id) {
        return genresStorage.getGenreById(id);
    }
}
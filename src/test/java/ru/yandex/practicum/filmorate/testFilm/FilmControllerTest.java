package ru.yandex.practicum.filmorate.testFilm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.DoesNotExistException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import static org.junit.jupiter.api.Assertions.*;


import static org.junit.jupiter.api.Assertions.assertEquals;


import java.time.LocalDate;
import java.util.*;

public class FilmControllerTest {
    FilmController controller;
    Film testFilm;

    @BeforeEach
    public void beforeEach() {
        User testUser = new User(1, "Mira-Mira", "Mor", (LocalDate.of(2000,12,12)),
                "mrrr@mail.ru");
        User testUser2 = new User(2, "Zireael", "Kharada", (LocalDate.of(2000,12,12)),
                "lastochka@mail.ru");
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        userStorage.createUser(testUser);
        userStorage.createUser(testUser2);
        controller = new FilmController(new FilmService(new InMemoryFilmStorage(), userStorage));
        testFilm = new Film(1, "Don't look up", "Bla-bla-bla", (LocalDate.of(2022,5,12)),
                100, 1, Set.of(1));
    }

    // GET
    @Test
    public void getListOfFilms() throws ValidationException {
        List<Film> testFilmsArray = new ArrayList<>();
        assertEquals(controller.getFilms(), testFilmsArray);

        testFilmsArray.add(testFilm);
        controller.addFilm(testFilm);
        assertEquals(controller.getFilms(), testFilmsArray);
    }

    //POST
    @Test
    public void testAddFilmWithEmptyName() {
        testFilm.setName("");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(testFilm));
        assertEquals("Поле с названием фильма не должно быть пустым.", exception.getMessage());
    }

    @Test
    public void testAddFilmWithDescriptionMoreThan200Symbols() {
        testFilm.setDescription("Once upon a time Once upon a timeOnce upon a timeOnce upon a time" +
                "Once upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce " +
                "upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a time" +
                "Once upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a time" +
                "Once upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a time" +
                "Once upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a time" +
                "Once upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a time");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(testFilm));
        assertEquals("Превышен лимит символов для описания фильма. Максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    public void testAddFilmWithIncorrectReleaseDate() {
        testFilm.setReleaseDate(LocalDate.of(1800,4,14));
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(testFilm));
        assertEquals("Дата релиза фильма не должна быть раньше 28 декабря 1895 года.", exception.getMessage());
    }

    @Test
    public void testAddFilmWithIncorrectDuration() {
        testFilm.setDuration(0);
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(testFilm));
        assertEquals("Продолжительность фильма не может быть отрицательной или равной нулю.", exception.getMessage());
    }

    //PUT
    @Test
    public void testUpdateFilmWithIncorrectId() throws ValidationException {
        controller.addFilm(testFilm);

        Film testFilm2 = new Film(100, "Don't look up", "Bla-bla-bla", (LocalDate.of(2022,5,12)),
                100, 1, Set.of(1));

        DoesNotExistException exception = assertThrows(DoesNotExistException.class, () -> controller.update(testFilm2));
        assertEquals("Фильм с указанным id не существует.", exception.getMessage());
    }

    @Test
    public void testUpdateFilm() throws ValidationException {
        controller.addFilm(testFilm);
        Film testFilm2 = new Film(1, "hfvjvfjjvk", "Bla-bla-bla", (LocalDate.of(2022,9,11)),
                100, 1, Set.of(1));
        controller.update(testFilm2);
        List<Film> testFilms = new ArrayList<>();
        testFilms.add(testFilm2);

        assertEquals(testFilms, controller.getFilms());
    }

    @Test
    public void testGetFilm() {
        DoesNotExistException exception = assertThrows(DoesNotExistException.class, () ->controller.getFilm(8));
        assertEquals("Фильм с указанным id не существует.", exception.getMessage());
        controller.addFilm(testFilm);
        assertEquals(testFilm, controller.getFilm(testFilm.getId()));
    }

    @Test
    public void testAddLike() {
        User testUser = new User(1, "Mira-Mira", "Mor", (LocalDate.of(2000,12,12)),
                "mrrr@mail.ru");
        controller.addFilm(testFilm);
        Set<Long> testLikes = new HashSet<>();
        assertEquals(testLikes, controller.getFilm(testFilm.getId()).getLikes());

        testFilm.getLikes().add(testUser.getId());
        testLikes.add(testUser.getId());
        assertEquals(testLikes, controller.getFilm(testFilm.getId()).getLikes());
    }

    @Test
    public void testDeleteLike() {
        controller.addFilm(testFilm);
        controller.addLike(testFilm.getId(), 2L);
        controller.deleteLike(testFilm.getId(), 2L);
        Set<Long> testLikes = new HashSet<>();
        assertEquals(testLikes, controller.getFilm(testFilm.getId()).getLikes());

    }

    @Test
    public void testGetPopularFilms() {
        Film testFilm2 = new Film(2, "hfvjvfjjvk", "Bla-bla-bla", (LocalDate.of(2022,9,11)),
                100, 1, Set.of(1));
       controller.addFilm(testFilm);
       controller.addFilm(testFilm2);
       controller.addLike(1, 1);
       controller.addLike(1, 2);
       controller.addLike(2, 1);
       List<Film> testList = new ArrayList<>();
       testList.add(testFilm);
       testList.add(testFilm2);
       assertEquals(testList, controller.getPopularFilms(2, "desc"));

        IncorrectParameterException exception = assertThrows(IncorrectParameterException.class, ()-> controller.getPopularFilms(0, "desc"));
        assertEquals("count. Значение параметра запроса не должно быть меньше 1", exception.getParameter());

        exception = assertThrows(IncorrectParameterException.class, ()-> controller.getPopularFilms(2, "popa"));
        assertEquals("sort. Введите один из предложенных вариантов: asc или desc.", exception.getParameter());
    }
}
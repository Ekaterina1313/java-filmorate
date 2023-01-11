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
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        InMemoryFilmStorage storage = new InMemoryFilmStorage();
        User testUser = User.builder().email("mirasolar@mail.ru")
                .login("Mira-Mira")
                .birthday(LocalDate.of(2000,12,12))
                .name("Mira")
                .build();
        User testUser2 = User.builder().email("mirasolar@mail.ru")
                .login("Mira-Mira")
                .birthday(LocalDate.of(2000,12,12))
                .name("Mira")
                .build();
        userStorage.createUser(testUser);
        userStorage.createUser(testUser2);
        controller = new FilmController(storage, new FilmService(storage), userStorage);
        testFilm = Film.builder()
                .releaseDate(LocalDate.of(2001,12,12))
                .name("Star Wars")
                .description("Bla-bla-bla")
                .duration(180)
                .build();
    }

    // GET
    @Test
    public void getListOfFilms() throws ValidationException {
        List<Film> testFilmsArray = new ArrayList<>();
        assertEquals(controller.getFilms(), testFilmsArray);

        testFilmsArray.add(testFilm);
        controller.add(testFilm);
        assertEquals(controller.getFilms(), testFilmsArray);
    }

    //POST
    @Test
    public void testAddFilmWithEmptyName() {
        testFilm.setName("");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.add(testFilm));
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
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.add(testFilm));
        assertEquals("Превышен лимит символов для описания фильма. Максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    public void testAddFilmWithIncorrectReleaseDate() {
        testFilm.setReleaseDate(LocalDate.of(1800,4,14));
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.add(testFilm));
        assertEquals("Дата релиза фильма не должна быть раньше 28 декабря 1895 года.", exception.getMessage());
    }

    @Test
    public void testAddFilmWithIncorrectDuration() {
        testFilm.setDuration(0);
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.add(testFilm));
        assertEquals("Продолжительность фильма не может быть отрицательной или равной нулю.", exception.getMessage());
    }

    //PUT
    @Test
    public void testUpdateFilmWithIncorrectId() throws ValidationException {
        controller.add(testFilm);

        Film testFilm2 = Film.builder()
                .releaseDate(LocalDate.of(2022,5,12))
                .name("Don't look up")
                .description("Bla-bla-bla")
                .duration(120)
                .id(5)
                .build();

        DoesNotExistException exception = assertThrows(DoesNotExistException.class, () -> controller.update(testFilm2));
        assertEquals("Фильм с указанным id не существует.", exception.getMessage());
    }

    @Test
    public void testUpdateFilm() throws ValidationException {
        controller.add(testFilm);
        Film testFilm2 = Film.builder()
                .releaseDate(LocalDate.of(2020,12,12))
                .name("Some Film")
                .description("Bla-bla-bla")
                .duration(120)
                .id(testFilm.getId())
                .build();
        controller.update(testFilm2);
        List<Film> testFilms = new ArrayList<>();
        testFilms.add(testFilm2);

        assertEquals(testFilms, controller.getFilms());
    }

    @Test
    public void testGetFilm() {
        DoesNotExistException exception = assertThrows(DoesNotExistException.class, () ->controller.getFilm(8));
        assertEquals("Фильм с указанным id не существует.", exception.getMessage());
        controller.add(testFilm);
        assertEquals(testFilm, controller.getFilm(testFilm.getId()));
    }

    @Test
    public void testAddLike() {
        User testUser = User.builder().email("mirasolar@mail.ru")
                .login("Mira-Mira")
                .birthday(LocalDate.of(2000,12,12))
                .name("Mira")
                .build();
        controller.add(testFilm);
        Set<Long> testLikes = new HashSet<>();
        assertEquals(testLikes, controller.getFilm(testFilm.getId()).getLikes());

        testFilm.getLikes().add(testUser.getId());
        testLikes.add(testUser.getId());
        assertEquals(testLikes, controller.getFilm(testFilm.getId()).getLikes());
    }

    @Test
    public void testDeleteLike() {
        controller.add(testFilm);
        controller.addLike(testFilm.getId(), 2L);
        controller.deleteLike(testFilm.getId(), 2L);
        Set<Long> testLikes = new HashSet<>();
        assertEquals(testLikes, controller.getFilm(testFilm.getId()).getLikes());

    }

    @Test
    public void testGetPopularFilms() {
       Film testFilm2 = Film.builder()
                .releaseDate(LocalDate.of(2001,12,12))
                .name("Star Wars")
                .description("Bla-bla-bla")
                .duration(180)
                .build();
       controller.add(testFilm);
       controller.add(testFilm2);
       controller.addLike(1, 1);
       controller.addLike(1, 2);
       controller.addLike(2, 1);
       List<Film> testList = new ArrayList<>();
       testList.add(testFilm);
       testList.add(testFilm2);
       assertEquals(testList, controller.getPopularFilms(2, "desc"));

        IncorrectParameterException exception = assertThrows(IncorrectParameterException.class, ()-> controller.getPopularFilms(0, "desc"));
        assertEquals("count", exception.getParameter());

        exception = assertThrows(IncorrectParameterException.class, ()-> controller.getPopularFilms(2, "popa"));
        assertEquals("sort", exception.getParameter());
    }
}
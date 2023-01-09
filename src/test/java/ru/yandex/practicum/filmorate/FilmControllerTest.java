package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.FileDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import static org.junit.jupiter.api.Assertions.*;


import static org.junit.jupiter.api.Assertions.assertEquals;


import java.time.LocalDate;
import java.util.*;

public class FilmControllerTest {
    FilmController controller;
    Film testFilm;
    @BeforeEach
    public void beforeEach() {
        controller = new FilmController();
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

        Film testFilm2 = Film.builder()
                .releaseDate(LocalDate.of(2022,5,12))
                .name("Don't look up")
                .description("Bla-bla-bla")
                .duration(120)
                .id(5)
                .build();

        FileDoesNotExistException exception = assertThrows(FileDoesNotExistException.class, () -> controller.updateFilm(testFilm2));
        assertEquals("Фильм с указанным id не существует.", exception.getMessage());
    }

    @Test
    public void testUpdateFilm() throws ValidationException {
        controller.addFilm(testFilm);
        Film testFilm2 = Film.builder()
                .releaseDate(LocalDate.of(2020,12,12))
                .name("Some Film")
                .description("Bla-bla-bla")
                .duration(120)
                .id(testFilm.getId())
                .build();
        controller.updateFilm(testFilm2);
        List<Film> testFilms = new ArrayList<>();
        testFilms.add(testFilm2);

        assertEquals(testFilms, controller.getFilms());
    }
}
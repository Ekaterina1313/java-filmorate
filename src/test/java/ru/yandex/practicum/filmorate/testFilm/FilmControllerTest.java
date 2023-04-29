package ru.yandex.practicum.filmorate.testFilm;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenresDbStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesDBStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingsDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.Constants.DESCENDING_ORDER;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerTest {
    FilmController controller;
    Film testFilm1;
    Film testFilm2;
    User testUser1;
    User testUser2;
    Genre testGenre1;
    Genre testGenre2;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void beforeEach() {
        controller = new FilmController(new FilmService(new FilmDbStorage(jdbcTemplate), new UserDbStorage(jdbcTemplate),
                new LikesDBStorage(jdbcTemplate), new GenresDbStorage(jdbcTemplate), new RatingsDbStorage(jdbcTemplate)));

        jdbcTemplate.execute("delete from  film_genres");
        jdbcTemplate.execute("delete from likes");
        jdbcTemplate.execute("delete from films");
        jdbcTemplate.execute("delete from users");
        testFilm1 = new Film(1, "The Matrix", "la-la-la", LocalDate.of(1999, 01, 01),
                200, new Rating(1, "G"), new LinkedHashSet<>());
        testFilm2 = new Film(2, "ko-ko-ko", "ta-ta-ta", LocalDate.of(1990, 02, 11),
                205, new Rating(2, "PG"), new LinkedHashSet<>());

        testUser1 = new User(1, "Nana", "lunar", LocalDate.of(1990, 12, 12),
                "nana@mail.ru");
        testUser2 = new User(2, "Mira", "Mira", LocalDate.of(1995, 11, 11),
                "mira@mail.ru");

        jdbcTemplate.update("insert into users (id, name, login, birthday, email) " +
                "values (1, 'Nana', 'lunar', '1990-12-12', 'nana@mail.ru')");
        jdbcTemplate.update("insert into users (id, name, login, birthday, email) " +
                "values (2, 'Mira', 'Mira', '1995-11-11', 'mira@mail.ru')");

        testGenre1 = new Genre(1, "Комедия");
        testGenre2 = new Genre(2, " Драма");
    }

    @Test
    public void testAddFilm() {
        Film savedFilm = controller.addFilm(testFilm1);

        // проверим, что фильм сохранен в бд
        assertNotNull(savedFilm.getId());
        assertEquals(testFilm1.getName(), savedFilm.getName());
        assertEquals(testFilm1.getDescription(), savedFilm.getDescription());
        assertEquals(testFilm1.getReleaseDate(), savedFilm.getReleaseDate());
        assertEquals(testFilm1.getDuration(), savedFilm.getDuration());
        assertEquals(testFilm1.getMpa().getId(), savedFilm.getMpa().getId());

        // проверим, что жанры сохранились бд
        Set<Genre> testGenres = new LinkedHashSet<>();
        testGenres.add(testGenre1);
        testGenres.add(testGenre2);
        savedFilm.setGenres(testGenres);
        Film savedFilmWithGenres = controller.addFilm(savedFilm);

        assertNotNull(savedFilmWithGenres.getId());
        assertEquals(testFilm1.getName(), savedFilmWithGenres.getName());
        assertEquals(testFilm1.getDescription(), savedFilmWithGenres.getDescription());
        assertEquals(testFilm1.getReleaseDate(), savedFilmWithGenres.getReleaseDate());
        assertEquals(testFilm1.getDuration(), savedFilmWithGenres.getDuration());
        assertEquals(testFilm1.getMpa().getId(), savedFilmWithGenres.getMpa().getId());
        assertEquals(testGenres, savedFilmWithGenres.getGenres());
    }

    @Test
    public void testGetFilms() {
        controller.addFilm(testFilm1);
        controller.addFilm(testFilm2);
        List<Film> films = controller.getFilms();

        assertNotNull(films);
        assertEquals(2, films.size());
        assertTrue(films.contains(testFilm1));

        Film filmFromDb1 = films.get(0);
        assertNotNull(filmFromDb1);
        assertEquals(testFilm1.getName(), filmFromDb1.getName());
        assertEquals(testFilm1.getDescription(), filmFromDb1.getDescription());
        assertEquals(testFilm1.getReleaseDate(), filmFromDb1.getReleaseDate());
        assertEquals(testFilm1.getDuration(), filmFromDb1.getDuration());
        assertEquals(testFilm1.getMpa(), filmFromDb1.getMpa());
        assertEquals(testFilm1.getGenres(), filmFromDb1.getGenres());
    }

    @Test
    public void testUpdateFilm() {
        Set<Genre> genres = new LinkedHashSet<>();
        genres.add(testGenre1);
        genres.add(testGenre2);
        testFilm1.setGenres(genres);

        controller.addFilm(testFilm1);

        testFilm1.setName("The Matrix Reloaded");
        testFilm1.setDescription("lorem ipsum");
        testFilm1.setReleaseDate(LocalDate.of(2003, 05, 15));
        testFilm1.setDuration(138);
        testFilm1.setMpa(new Rating(2, "PG-13"));
        testFilm1.getGenres().remove(testGenre1);
        testFilm1.getGenres().add(new Genre(3, "Мультфильм"));

        controller.update(testFilm1);

        Film updatedFilm = controller.getFilm(testFilm1.getId());
        assertEquals(testFilm1, updatedFilm);
    }

    @Test
    public void testGetFilmById() {
        testFilm1.getGenres().add(new Genre(1, "Комедия"));
        testFilm1.getGenres().add(new Genre(2, "Драма"));
        controller.addFilm(testFilm1);

        Film actualFilm = controller.getFilm(testFilm1.getId());

        assertEquals(testFilm1, actualFilm);
    }

    @Test
    public void testAddLike() {
        controller.addFilm(testFilm1);
        long filmId = testFilm1.getId();
        long userId = testUser1.getId();

        controller.addLike(filmId, userId);

        SqlRowSet result = jdbcTemplate.queryForRowSet("select * from likes where film_id = ? and user_id = ?", filmId, userId);
        assertTrue(result.next());
    }

    @Test
    public void testDeleteLike() {
        controller.addFilm(testFilm1);
        long filmId = testFilm1.getId();
        long userId = testUser1.getId();

        controller.addLike(filmId, userId);
        controller.deleteLike(filmId, userId);
        SqlRowSet result = jdbcTemplate.queryForRowSet("select * from likes where film_id = ? and user_id = ?", filmId, userId);
        assertFalse(result.next());

    }

    @Test
    public void testGetTheMostPopularFilms() {
        controller.addFilm(testFilm1);
        controller.addFilm(testFilm2);
        controller.addLike(testFilm1.getId(), testUser1.getId());
        controller.addLike(testFilm1.getId(), testUser2.getId());
        controller.addLike(testFilm2.getId(), testUser1.getId());

        List<Film> expectedFilms = new ArrayList<>();
        expectedFilms.add(testFilm1);
        expectedFilms.add(testFilm2);

        List<Film> actualFilms = controller.getPopularFilms(2, DESCENDING_ORDER);

        assertEquals(expectedFilms.size(), actualFilms.size());
        for (int i = 0; i < expectedFilms.size(); i++) {
            Film expectedFilm = expectedFilms.get(i);
            Film actualFilm = actualFilms.get(i);
            assertEquals(expectedFilm.getName(), actualFilm.getName());
            assertEquals(expectedFilm.getDescription(), actualFilm.getDescription());
            assertEquals(expectedFilm.getReleaseDate(), actualFilm.getReleaseDate());
            assertEquals(expectedFilm.getDuration(), actualFilm.getDuration());
            assertEquals(expectedFilm.getMpa().getId(), actualFilm.getMpa().getId());
            assertEquals(expectedFilm.getGenres(), actualFilm.getGenres());
        }
    }

    // validation
    @Test
    public void testAddFilmWithEmptyName() {
        testFilm1.setName("");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(testFilm1));
        assertEquals("Поле с названием фильма не должно быть пустым.", exception.getMessage());
    }

    @Test
    public void testAddFilmWithDescriptionMoreThan200Symbols() {
        testFilm1.setDescription("Once upon a time Once upon a timeOnce upon a timeOnce upon a time" +
                "Once upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce " +
                "upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a time" +
                "Once upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a time" +
                "Once upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a time" +
                "Once upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a time" +
                "Once upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a timeOnce upon a time");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(testFilm1));
        assertEquals("Описание фильма не должно быть пустым или превышать лимит символов. Максимальная длина описания - 200 символов.",
                exception.getMessage());
    }

    @Test
    public void testAddFilmWithIncorrectReleaseDate() {
        testFilm1.setReleaseDate(LocalDate.of(1800, 4, 14));
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(testFilm1));
        assertEquals("Дата релиза фильма не должна быть пустой и должна быть не раньше 28 декабря 1895 года.", exception.getMessage());
    }

    @Test
    public void testAddFilmWithIncorrectDuration() {
        testFilm1.setDuration(0);
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(testFilm1));
        assertEquals("Продолжительность фильма не может быть отрицательной или равной нулю.", exception.getMessage());

        testFilm1.setDuration(-500);
        exception = assertThrows(ValidationException.class, () -> controller.addFilm(testFilm1));
        assertEquals("Продолжительность фильма не может быть отрицательной или равной нулю.", exception.getMessage());
    }

    //
    @Test
    public void testUpdateFilmWithIncorrectId() throws ValidationException {
        Film testFilm3 = new Film(2000000, "ko-ko-ko", "ta-ta-ta", LocalDate.of(1990, 02, 11),
                205, new Rating(2, "PG"), new LinkedHashSet<>());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> controller.update(testFilm3));
        assertEquals("Фильм с указанным id не существует.", exception.getMessage());
    }

    @Test
    public void testGetPopularFilmsWithIncorrectParameters() {
        IncorrectParameterException exception = assertThrows(IncorrectParameterException.class, () -> controller.getPopularFilms(0, "desc"));
        assertEquals("count. Значение параметра запроса не должно быть меньше 1", exception.getParameter());

        exception = assertThrows(IncorrectParameterException.class, () -> controller.getPopularFilms(2, "pops"));
        assertEquals("sort. Введите один из предложенных вариантов: asc или desc.", exception.getParameter());
    }
}
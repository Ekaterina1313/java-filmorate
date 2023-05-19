package ru.yandex.practicum.filmorate.testGenre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.GenreController;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.mapper.RatingRowMapper;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenresDbStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesDBStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingsDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreControllerTest {
    GenreController controller;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private FilmRowMapper filmRowMapper;
    @Autowired
    private GenreRowMapper genreRowMapper;
    @Autowired
    private UserRowMapper userRowMapper;
    @Autowired
    private RatingRowMapper ratingRowMapper;

    @BeforeEach
    public void beforeEach() {
        controller = new GenreController(new FilmService(new FilmDbStorage(jdbcTemplate, filmRowMapper, genreRowMapper), new UserDbStorage(jdbcTemplate, userRowMapper),
                new LikesDBStorage(jdbcTemplate, filmRowMapper), new GenresDbStorage(jdbcTemplate, genreRowMapper), new RatingsDbStorage(jdbcTemplate, ratingRowMapper)));
        jdbcTemplate.execute("delete from genres");
        jdbcTemplate.execute("insert into genres (genre_id, genre_name) values (1, 'Комедия')");
        jdbcTemplate.execute("insert into genres (genre_id, genre_name) values (2, 'Драма')");
        jdbcTemplate.execute("insert into genres (genre_id, genre_name) values (3, 'Триллер')");
    }

    @Test
    public void getListOfGenresTest() {
        List<Genre> genres = controller.getListOfGenre();
        assertEquals(3, genres.size());
        assertEquals("Комедия", genres.get(0).getName());
        assertEquals("Драма", genres.get(1).getName());
        assertEquals("Триллер", genres.get(2).getName());
    }

    @Test
    public void getGenreByIdTest() {
        Genre genre = controller.getGenreById(1L);
        assertEquals(1L, genre.getId());
        assertEquals("Комедия", genre.getName());
    }
}
package ru.yandex.practicum.filmorate.testRating;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.RatingController;
import ru.yandex.practicum.filmorate.model.Rating;
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
public class RatingControllerTest {

    RatingController controller;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        controller = new RatingController(new FilmService(new FilmDbStorage(jdbcTemplate), new UserDbStorage(jdbcTemplate),
                new LikesDBStorage(jdbcTemplate), new GenresDbStorage(jdbcTemplate), new RatingsDbStorage(jdbcTemplate)));
        jdbcTemplate.execute("delete from rating");
        jdbcTemplate.execute("insert into rating (rating_id, rating_name) values (1, 'PG-13')");
        jdbcTemplate.execute("insert into rating (rating_id, rating_name) values (2, 'PG')");
        jdbcTemplate.execute("insert into rating (rating_id, rating_name) values (3, 'G')");
    }

    @Test
    void getListOfRatingTest() {
        List<Rating> rating = controller.getListOfRating();
        assertEquals(3, rating.size());
        assertEquals("PG-13", rating.get(0).getName());
        assertEquals("PG", rating.get(1).getName());
        assertEquals("G", rating.get(2).getName());
    }

    @Test
    void getGenreByIdTest() {
        Rating rating = controller.getRatingById(1);
        assertEquals(1L, rating.getId());
        assertEquals("PG-13", rating.getName());
    }
}
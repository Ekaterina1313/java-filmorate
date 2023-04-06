package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.ArrayList;
import java.util.List;

@Component
public class RatingsDbStorage implements RatingsStorage {
    private final JdbcTemplate jdbcTemplate;

    public RatingsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Rating> getListOfRating() {
        List<Rating> listOfRating = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from rating");
        while (filmRows.next()) {
            Rating rating = new Rating(filmRows.getInt("rating_id"), filmRows.getString("rating_name"));
            listOfRating.add(rating);
        }
        return listOfRating;
    }

    @Override
    public Rating getRatingById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from rating where rating_id = ?", id);
        if (filmRows.next()) {
            return new Rating(id, filmRows.getString("rating_name"));
        } else {
            throw new EntityNotFoundException("Рейтинг с указанным id не существует.");
        }
    }
}

package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.mapper.RatingRowMapper;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

@Component
public class RatingsDbStorage implements RatingsStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RatingRowMapper ratingRowMapper;

    public RatingsDbStorage(JdbcTemplate jdbcTemplate, RatingRowMapper ratingRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.ratingRowMapper = ratingRowMapper;
    }

    @Override
    public List<Rating> getListOfRating() {
        String sql = "select * from rating";
        return jdbcTemplate.query(sql, new RatingRowMapper());
    }

    @Override
    public Rating getRatingById(int id) {
        List<Rating> ratingList = jdbcTemplate.query("select * from rating where rating_id = ?", new Object[]{id}, ratingRowMapper);
        if (ratingList.isEmpty()) {
            throw new EntityNotFoundException("Рейтинг с указанным id не существует.");
        }
        return ratingList.get(0);
    }
}
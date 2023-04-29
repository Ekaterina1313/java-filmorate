package ru.yandex.practicum.filmorate.storage.likes;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Component
public class LikesDBStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    public LikesDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(long filmId, long userId) {
        String sqlQuery = "insert into likes(film_id, user_id)values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        String sqlQuery = "delete from likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public List<Film> getTheMostPopularFilms(int count) {
        List<Film> theMostPopularFilms = new ArrayList<>();
        SqlRowSet likesRowSet = jdbcTemplate.queryForRowSet("select id, F.name, F.description, F.release_date, " +
                "F.duration, F.rating_id, R.rating_name, " +
                "count(user_id) from films as F " +
                "left outer join likes as L on F.id = L.film_id " +
                "left outer join rating as R on F.rating_id = R.rating_id " +
                "group by id " +
                "order by count(user_id) desc " +
                "limit ?", count);
        while (likesRowSet.next()) {
            Film film = new Film(likesRowSet.getLong("id"),
                    likesRowSet.getString("name"),
                    likesRowSet.getString("description"),
                    likesRowSet.getDate("release_date").toLocalDate(),
                    likesRowSet.getInt("duration"),
                    new Rating(likesRowSet.getInt("rating_id"), likesRowSet.getString("rating_name")),
                    new LinkedHashSet<>());
            theMostPopularFilms.add(film);
        }
        return theMostPopularFilms;
    }
}
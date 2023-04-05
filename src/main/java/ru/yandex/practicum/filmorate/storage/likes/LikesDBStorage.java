package ru.yandex.practicum.filmorate.storage.likes;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LikesDBStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;

    public LikesDBStorage(JdbcTemplate jdbcTemplate, FilmDbStorage filmDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmDbStorage = filmDbStorage;
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
        Map<Long, Film> allFilms = filmDbStorage.getFilms();
        SqlRowSet likesRowSet = jdbcTemplate.queryForRowSet("select id, count(user_id) from films as F " +
                "left outer join likes as L on F.id = L.film_id " +
                "group by id " +
                "order by count(user_id) desc " +
                "limit ?", count);
        while (likesRowSet.next()) {
            theMostPopularFilms.add(allFilms.get(likesRowSet.getLong("id")));
        }
        return theMostPopularFilms;
    }
}

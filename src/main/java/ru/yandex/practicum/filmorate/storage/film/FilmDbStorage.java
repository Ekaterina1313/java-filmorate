package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, Film> getFilms() {
        Map<Long, Film> films = new HashMap<>();
        String sql = "select * from films AS F left outer join rating AS R ON F.rating_id = R.rating_id";
        List<Film> filmList = jdbcTemplate.query(sql, new FilmRowMapper());
        for (Film film : filmList) {
            films.put(film.getId(), film);
        }
        String genresSql = "select * from film_genres AS FG left outer join genres AS G ON FG.genre_id = G.genre_id";
        SqlRowSet genresRows = jdbcTemplate.queryForRowSet(genresSql);
        while (genresRows.next()) {
            films.get(genresRows.getLong("film_id")).getGenres().add(new Genre(
                    genresRows.getLong("genre_id"),
                    genresRows.getString("genre_name")));
        }
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("insert into films (name, description, release_date, duration, rating_id) " +
                    "values (?, ?, ?, ?, ?)", new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        long id = keyHolder.getKey().longValue();
        film.setId(id);
        if (film.getGenres() != null) {
            addGenreToDB(film.getGenres(), id);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?" +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() != null) {
            Map<Long, Genre> mapOfGenres = new HashMap<>();
            for (Genre element : film.getGenres()) { // заполним мапу уникальными элементами
                if (!mapOfGenres.containsKey(element.getId())) {
                    mapOfGenres.put(element.getId(), element);
                }
            }
            Set<Genre> uniqueGenres = new LinkedHashSet<>(mapOfGenres.values());
            film.setGenres(uniqueGenres);
            updateGenres(film.getGenres(), film.getId());
        }
        return film;
    }

    @Override
    public Film getFilmById(long id) {
        String sql = "select * from films as F " +
                "left outer join rating as R on F.rating_id = R.rating_id " +
                "where id = ?";
        Film film = jdbcTemplate.queryForObject(sql, new Object[]{id}, new FilmRowMapper());

        sql = "select * from film_genres as FG " +
                "left outer join genres as G on FG.genre_id = G.genre_id " +
                "where film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, new Object[]{id}, new GenreRowMapper());
        film.setGenres(new LinkedHashSet<>(genres));
        return film;
    }

    private void updateGenres(Set<Genre> genresId, long id) {
        String sqlDelete = "delete from film_genres where film_id = ?";
        jdbcTemplate.update(sqlDelete, id);
        addGenreToDB(genresId, id);
    }

    private void addGenreToDB(Set<Genre> genres, long id) {
        if (!genres.isEmpty()) {
            List<Object[]> batchArgs = new ArrayList<>();
            for (Genre genre : genres) {
                batchArgs.add(new Object[]{id, genre.getId()});
            }
            String sql = "insert into film_genres (film_id, genre_id) values (?, ?)";
            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
    }

    @Override
    public boolean isContainFilm(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films where id = ?", id);
        return filmRows.next();
    }
}
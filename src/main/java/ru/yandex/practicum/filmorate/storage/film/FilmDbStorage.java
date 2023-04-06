package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.genre.GenresStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenresStorage genresStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenresStorage genresStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genresStorage = genresStorage;
    }

    @Override
    public Map<Long, Film> getFilms() {
        Map<Long, Film> sqlFilm = new HashMap<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films as F left outer join rating as R on F.rating = R.rating_id");
        SqlRowSet genresRows = jdbcTemplate.queryForRowSet("select * from film_genres as FG " +
                "left outer join genres as G on FG.genre_id = G.genre_id");

        while (filmRows.next()) {
            Film film = new Film(filmRows.getLong("id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getInt("duration"),
                    new Rating(filmRows.getInt("rating"), filmRows.getString("rating_name")),
                    new LinkedHashSet<>());
            sqlFilm.put(film.getId(), film);
        }
        while (genresRows.next()) {
            sqlFilm.get(genresRows.getLong("film_id")).getGenres().add(new Genre(genresRows.getInt("genre_id"),
                    genresRows.getString("genre_name")));
        }
        return sqlFilm;
    }

    @Override
    public Film addFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("insert into films (name, description, release_date, duration, rating) " +
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
            genresStorage.addGenreToDB(film.getGenres(), id);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating = ?" +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() != null) {
            Map<Integer, Genre> mapOfGenres = new HashMap<>();
            for (Genre element : film.getGenres()) { // заполним мапу уникальными элементами
                if (!mapOfGenres.containsKey(element.getId())) {
                    mapOfGenres.put(element.getId(), element);
                }
            }
            Set<Genre> uniqueGenres = new LinkedHashSet<>(mapOfGenres.values());
            film.setGenres(uniqueGenres);
            genresStorage.updateGenres(film.getGenres(), film.getId());
        }
        return film;
    }

    @Override
    public Film getFilmById(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films as F " +
                "left outer join rating as R on f.rating = R.rating_id " +
                "where F.id = ?", id);
        filmRows.next();
        Film film = new Film(filmRows.getLong("id"),
                filmRows.getString("name"),
                filmRows.getString("description"),
                filmRows.getDate("release_date").toLocalDate(),
                filmRows.getInt("duration"),
                new Rating(filmRows.getInt("rating"), filmRows.getString("rating_name")),
                new LinkedHashSet<>());

        SqlRowSet genresRows = jdbcTemplate.queryForRowSet("select * from film_genres as FG " +
                "left outer join genres as G on FG.genre_id = G.genre_id " +
                "where film_id = ?", id);
        while (genresRows.next()) {
            film.getGenres().add(new Genre(genresRows.getInt("genre_id"), genresRows.getString("genre_name")));
        }
        return film;
    }

    @Override
    public boolean isContainFilm(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films where id = ?", id);
        return filmRows.next();
    }
}
package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.RatingDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private long id = 1;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, Film> getFilms() {
        Map<Long, Film> sqlFilm = new HashMap<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films as F left outer join rating as R on F.rating = R.rating_id");
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet("select * from likes");
        SqlRowSet genresRows = jdbcTemplate.queryForRowSet("select * from film_genres as FG " +
                "left outer join genres as G on FG.genre_id = G.genre_id");

        while (filmRows.next()) {
            Film film = new Film(filmRows.getLong("id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getInt("duration"),
                    new Rating(filmRows.getInt("rating"), filmRows.getString("rating_name")),
                    new ArrayList<>());
            sqlFilm.put(film.getId(), film);
        }
        while (likesRows.next()) {
            sqlFilm.get(likesRows.getLong("film_id")).getLikes().add(likesRows.getLong("user_id"));
        }
        while (genresRows.next()) {
            sqlFilm.get(genresRows.getLong("film_id")).getGenres().add(new Genre(genresRows.getInt("genre_id"),
                    genresRows.getString("genre_name")));
        }
        return sqlFilm;
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(id);
        id++;
        String sqlQuery = "insert into films(id, name, description, release_date, duration, rating) " +
                "values (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
        if (film.getGenres() != null) {
            addGenreToDB(film.getGenres(), film.getId());
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
            List<Genre> uniqueGenres = new ArrayList<>(mapOfGenres.values());
            GenreComparator comparator = new GenreComparator();
            uniqueGenres.sort(comparator);
            film.setGenres(uniqueGenres);
            updateGenres(film.getGenres(), film.getId());
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
                new ArrayList<>());

        SqlRowSet likesRows = jdbcTemplate.queryForRowSet("select * from likes where film_id = ?", id);
        while (likesRows.next()) {
            film.getLikes().add(likesRows.getLong("user_id"));
        }

        SqlRowSet genresRows = jdbcTemplate.queryForRowSet("select * from film_genres as FG " +
                "left outer join genres as G on FG.genre_id = G.genre_id " +
                "where film_id = ?", id);
        while (genresRows.next()) {
            film.getGenres().add(new Genre(genresRows.getInt("genre_id"), genresRows.getString("genre_name")));
        }
        return film;
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
            throw new RatingDoesNotExistException("Рейтинг с указанным id не существует.");
        }
    }

    private void addGenreToDB(List<Genre> genres, long id) {
        for (Genre element : genres) {
            String sqlQuery = "insert into film_genres(film_id, genre_id)" +
                    "values (?, ?)";
            jdbcTemplate.update(sqlQuery, id, element.getId());
        }
    }

    private void updateGenres(List<Genre> genresId, long id) {
        List<Genre> genresFromDb = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film_genres where film_id = ?", id);
        while (filmRows.next()) {
            int sqlGenreId = filmRows.getInt("genre_id");
            if (genresId.contains(new Genre(sqlGenreId))) {
                genresFromDb.add(new Genre(sqlGenreId));
            } else {
                String sqlQuery = "delete from film_genres " +
                        "where film_genres_id = ?";
                jdbcTemplate.update(sqlQuery, filmRows.getInt("film_genres_id"));
            }
        }
        genresId.removeAll(genresFromDb);
        addGenreToDB(genresId, id);
    }

    @Override
    public List<Genre> getListOfGenre() {
        List<Genre> listOfGenres = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from genres order by genre_id asc");
        while (filmRows.next()) {
            Genre genre = new Genre(filmRows.getInt("genre_id"), filmRows.getString("genre_name"));
            listOfGenres.add(genre);
        }
        return listOfGenres;
    }

    @Override
    public Genre getGenreById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from genres where genre_id = ?", id);
        if (filmRows.next()) {
            return new Genre(filmRows.getInt("genre_id"), filmRows.getString("genre_name"));
        } else {
            throw new GenreDoesNotExistException("Жанр с указанным id не существует.");
        }
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
    public boolean isContainFilm(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films where id = ?", id);
        return filmRows.next();
    }
}
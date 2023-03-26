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
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.*;

@Slf4j
@Component ("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private long id = 1;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, Film> getFilms() {
        Map<Long, Film> sqlFilm = new HashMap<>();
        Set<Long> sqlLikes = new HashSet<>();
        Set<Integer> sqlGenreId = new HashSet<>();
        Film film = new Film(0, null, null, null, 0, 0, null);
        long firstId = 1;
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film as f" +
                "left outer join film_genres as fg on f.id=fg.film_id" +
                "left outer join likes as l on f.id=l.film_id" + " order by id asc");
        while (filmRows.next()) {
            if (filmRows.getLong("id") != firstId) {
                firstId = filmRows.getLong("id");
                film.setGenreId(sqlGenreId);
                film.setLikes(sqlLikes);
                sqlFilm.put(film.getId(), film);
                sqlLikes.clear();
                sqlGenreId.clear();
            }
            if (filmRows.getObject("user_id", Long.class) != null) {
                sqlLikes.add(filmRows.getObject("user_id", Long.class));
            }
            if (filmRows.getObject("genre_id") != null) {
                sqlGenreId.add(filmRows.getInt("genre_id"));
            }
            film.setId( filmRows.getLong("id"));
                    film.setName(filmRows.getString("name"));
                    film.setDescription( filmRows.getString("description"));
                    film.setReleaseDate(filmRows.getDate("releaseDate").toLocalDate());
                    film.setDuration(filmRows.getInt("duration"));
                    film.setRatingId( filmRows.getInt("rating_id"));
        }
        return sqlFilm;
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(id);
        id++;
        String sqlQuery = "insert into film(id, name, description, releaseDate, duration, ratingId) " +
                "values (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRatingId());
        addGenreToDB(film.getGenreId(), film.getId());
        return film;
    }

    private void addGenreToDB(Set<Integer> genreId, long id) {
        for (Integer element : genreId) {
            String sqlQuery = "insert into film_genres(film_id, genre_id)" +
                    "values (?, ?)";
            jdbcTemplate.update(sqlQuery, id, element);
        }
    }

    @Override
    public Film updateFilm(Film film) {
         String sqlQuery = "update film set" +
                 "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?" +
                 "where id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRatingId(),
                film.getId());
        updateGenres(film.getGenreId(), film.getId());
        return film;
    }

    private void updateGenres(Set<Integer> genresId, long id) {
        Set<Integer> genresFromDb = new HashSet<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film_genres where film_id = ?", id);
        while (filmRows.next()) {
            int sqlGenreId = filmRows.getInt("genre_id");
            if (genresId.contains(sqlGenreId)) {
                genresFromDb.add(sqlGenreId);
            } else {
                String sqlQuery = "delete from film_genres" +
                        "where film_genres_id = ?";
                jdbcTemplate.update(sqlQuery, filmRows.getInt("film_genre_id"));
            }
        }
        genresId.removeAll(genresFromDb);
        addGenreToDB(genresId, id);
    }

    @Override
    public Film getFilmById(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film as f" +
                "left outer join film_genres as fg on f.id=fg.film_id" +
                "left outer join likes as l on f.id=l.film_id" + " where f.id = ?", id);
        Set<Long> sqlLikes = new HashSet<>();
        Set<Integer> sqlGenreId = new HashSet<>();
        while (filmRows.next()) {
            if (filmRows.getObject("user_id", Long.class) != null) {
                sqlLikes.add(filmRows.getObject("user_id", Long.class));
            }
            if (filmRows.getObject("genre_id") != null) {
                sqlGenreId.add(filmRows.getInt("genre_id"));
            }
        }
        filmRows.first();
        Film film = new Film(
                filmRows.getLong("id"),
                filmRows.getString("name"),
                filmRows.getString("description"),
                filmRows.getDate("releaseDate").toLocalDate(),
                filmRows.getInt("duration"),
                filmRows.getInt("rating_id"),
                sqlGenreId);
                film.setLikes(sqlLikes);
        return film;
    }

    @Override
    public List<Rating> getListOfRating() {
    List<Rating> listOfRating = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from rating");
        while (filmRows.next()) {
            Rating rating = new Rating(filmRows.getInt("id"),
                    filmRows.getObject("name", RatingMPA.class));
            listOfRating.add(rating);
        }
        return listOfRating;
    }

    @Override
   public Rating getRatingById(int id) {
       SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from rating where rating_id = ?", id);
       if (filmRows.next()) {
           return new Rating(id, filmRows.getObject("name", RatingMPA.class));
       } else {
           throw new RatingDoesNotExistException("Рейтинг с указанным id не существует.");
       }
    }

    @Override
    public  List<Genre> getListOfGenre() {
        List<Genre> listOfGenres = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from genre");
        while (filmRows.next()) {
            Genre genre = new Genre(filmRows.getInt("id"),
                    filmRows.getString("name"));
            listOfGenres.add(genre);
        }
        return listOfGenres;
    }

    @Override
   public Genre getGenreById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from genre where genre_id = ?", id);
        if (filmRows.next()) {
            return new Genre(id, filmRows.getString("name"));
        } else {
            throw new GenreDoesNotExistException("Жанр с указанным id не существует.");
        }
    }

    @Override
    public void addLike(long filmId, long userId) {
        String sqlQuery = "insert into film_genres(film_id, user_id)" +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from likes where film_id = ? and user_id = ?", filmId, userId);
        String sqlQuery = "delete from likes where likes_id = ?";
        jdbcTemplate.update(sqlQuery, filmRows.getLong("likes_id"));
    }

    @Override
    public boolean isContainFilm(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film where id = ?", id);
        return filmRows.next();
    }
}
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
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films");
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet("select * from likes");
        SqlRowSet genresRows = jdbcTemplate.queryForRowSet("select * from film_genres");

        while (filmRows.next()) {
            Film film = new Film(filmRows.getLong("id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getInt("duration"),
                    filmRows.getInt("rating"),
                    new HashSet<>());
            sqlFilm.put(film.getId(), film);
        }
        while (likesRows.next()) {
            sqlFilm.get(likesRows.getLong("film_id")).getLikes().add(likesRows.getLong("user_id"));
        }
        while (genresRows.next()) {
            sqlFilm.get(genresRows.getLong("film_id")).getGenreId().add(genresRows.getInt("genre_id"));
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
         String sqlQuery = "update films set " +
                 "name = ?, description = ?, release_date = ?, duration = ?, rating = ?" +
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
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films");
        filmRows.next();
        Film film = new Film(filmRows.getLong("id"),
                filmRows.getString("name"),
                filmRows.getString("description"),
                filmRows.getDate("release_date").toLocalDate(),
                filmRows.getInt("duration"),
                filmRows.getInt("rating"),
                new HashSet<>());

        SqlRowSet likesRows = jdbcTemplate.queryForRowSet("select * from likes where film_id = ?", id);
        while (likesRows.next()) {
            film.getLikes().add(likesRows.getLong("user_id"));
        }

        SqlRowSet genresRows = jdbcTemplate.queryForRowSet("select * from film_genres where film_id = ?", id);
        while (genresRows.next()) {
            film.getGenreId().add(genresRows.getInt("genre_id"));
        }
        return film;
    }

    @Override
    public List<Rating> getListOfRating() {
    List<Rating> listOfRating = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from rating");
        while (filmRows.next()) {
            String ratingName = filmRows.getString("name");
            switch (ratingName) {
                case "G" :
                    Rating rating = new Rating(filmRows.getInt("rating_id"), RatingMPA.G);
                    listOfRating.add(rating);
                    break;
                case "PG" :
                    rating = new Rating(filmRows.getInt("rating_id"), RatingMPA.PG);
                    listOfRating.add(rating);
                    break;
                case "PG_13" :
                    rating = new Rating(filmRows.getInt("rating_id"), RatingMPA.PG_13);
                    listOfRating.add(rating);
                    break;
                case "R" :
                    rating = new Rating(filmRows.getInt("rating_id"), RatingMPA.R);
                    listOfRating.add(rating);
                    break;
                case "NC_17" :
                    rating = new Rating(filmRows.getInt("rating_id"), RatingMPA.NC_17);
                    listOfRating.add(rating);
                    break;
            }
        }
        return listOfRating;
    }

    @Override
   public Rating getRatingById(int id) {
       SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from rating where rating_id = ?", id);
       if (filmRows.next()) {
           String ratingName = filmRows.getString("name");
           RatingMPA nameMPA = null;
           switch (ratingName) {
               case "G" :
                  nameMPA = RatingMPA.G;
                   break;
               case "PG" :
                   nameMPA = RatingMPA.PG;
                   break;
               case "PG_13" :
                   nameMPA = RatingMPA.PG_13;
                   break;
               case "R" :
                   nameMPA =RatingMPA.R;
                   break;
               case "NC_17" :
                   nameMPA = RatingMPA.NC_17;
                   break;
           }
           return new Rating(id, nameMPA);
       } else {
           throw new RatingDoesNotExistException("Рейтинг с указанным id не существует.");
       }
    }

    @Override
    public  List<Genre> getListOfGenre() {
        List<Genre> listOfGenres = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from genres");
        while (filmRows.next()) {
            Genre genre = new Genre(filmRows.getInt("genre_id"),
                    filmRows.getString("name"));
            listOfGenres.add(genre);
        }
        return listOfGenres;
    }

    @Override
   public Genre getGenreById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from genres where genre_id = ?", id);
        if (filmRows.next()) {
            return new Genre(id, filmRows.getString("name"));
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
package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Component
public class GenresDbStorage implements GenresStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenresDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
            throw new EntityNotFoundException("Жанр с указанным id не существует.");
        }
    }

    @Override
    public void addGenreToDB(Set<Genre> genres, long id) {
        /*for (Genre element : genres) {
            String sqlQuery = "insert into film_genres(film_id, genre_id)" +
                    "values (?, ?)";
            jdbcTemplate.update(sqlQuery, id, element.getId());
        }*/
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
    public void updateGenres(Set<Genre> genresId, long id) {
       /* List<Genre> genresFromDb = new ArrayList<>();
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
    */
       Set<Genre> setToAdd = genresId;
        Set<Genre> setToRemove = new HashSet<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film_genres where film_id = ?", id);
        while (filmRows.next()) {
            Genre genre = new Genre(filmRows.getInt("genre_id"));
            if (setToAdd.contains(genre)) {
                setToAdd.remove(genre);
            } else {
                setToRemove.add(genre);
            }
        }
        if (!setToRemove.isEmpty()) {
            List<Object[]> batchArgs = new ArrayList<>();
            for (Genre genre : setToRemove) {
                batchArgs.add(new Object[]{id, genre.getId()});
            }
            String sql = "delete from film_genres where film_id = ? and genre_id = ?";
            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
        addGenreToDB(setToAdd, id);
    }
}

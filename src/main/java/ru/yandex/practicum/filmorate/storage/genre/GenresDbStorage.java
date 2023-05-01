package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
public class GenresDbStorage implements GenresStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;

    public GenresDbStorage(JdbcTemplate jdbcTemplate, GenreRowMapper genreRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreRowMapper = genreRowMapper;
    }

    @Override
    public List<Genre> getListOfGenre() {
        String sql = "select * from genres order by genre_id asc";
        List<Genre> listOfGenres = jdbcTemplate.query(sql, genreRowMapper);
        return listOfGenres;
    }

    @Override
    public Genre getGenreById(long id) {
        String sql = "select * from genres where genre_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, new Object[]{id}, genreRowMapper);
        if (genres.isEmpty()) {
            throw new EntityNotFoundException("Жанр с указанным id не существует.");
        } else {
            return genres.get(0);
        }
    }
}
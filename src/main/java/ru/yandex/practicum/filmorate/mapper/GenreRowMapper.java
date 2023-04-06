package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreRowMapper implements RowMapper<Genre> {

    @Override
    public Genre mapRow(ResultSet rs, int rowNum)  throws SQLException {
        Genre genre = new Genre(1, null);
        genre.setId(rs.getLong("genre_id"));
        genre.setName(rs.getString("genre_name"));
        return genre;
    }

}
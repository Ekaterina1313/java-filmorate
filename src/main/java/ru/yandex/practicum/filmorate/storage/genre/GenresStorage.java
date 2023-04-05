package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenresStorage {
    List<Genre> getListOfGenre();

    Genre getGenreById(int id);

    void addGenreToDB(Set<Genre> genres, long id);

    void updateGenres(Set<Genre> genresId, long id);
}

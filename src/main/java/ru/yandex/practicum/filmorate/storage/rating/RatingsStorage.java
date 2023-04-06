package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface RatingsStorage {
    List<Rating> getListOfRating();

    Rating getRatingById(int id);
}

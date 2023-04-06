package ru.yandex.practicum.filmorate.storage.friends;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {
    void addFriend(long from, long to);

    void deleteFriend(long from, long to);

    boolean isFriend(long firstId, long secondId);

    List<User> getAllFriends(long id);

    List<User> getListOfCommonFriends(long firstId, long secondId);
}

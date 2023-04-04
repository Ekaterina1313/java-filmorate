package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    Map<Long, User> getUsers();

    User createUser(User user);

    User updateUser(User user);

    boolean isContainId(long id);

    User getUserById(long id);

    void addFriend(long from, long to);

    void deleteFriend(long from, long to);

    boolean isFriend(long firstId, long secondId);

    List<User> getAllFriends(long id);

    List<User> getListOfFriendsById(List<Long> friends);

    List<User> getListOfCommonFriends(long firstId, long secondId);
}
package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Qualifier("inMemoryUserStorage")

public class InMemoryUserStorage implements UserStorage {

    private Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }

    @Override
    public User createUser(User user) throws ValidationException {
        user.setId(id);
        id++;
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(long id) {
        return users.get(id);
    }

    @Override
    public void addFriend(long from, long to) {

    }

    @Override
    public void deleteFriend(long from, long to) {

    }

    @Override
    public boolean isFriend(long first_id, long second_id) {
        return false;
    }

    @Override
    public List<Long> getAllFriends(long id) {
        return null;
    }

    @Override
    public List<User> getListOfFriendsById(List<Long> friends) {
        return null;
    }

    @Override
    public List<Long> getListOfCommonFriends(long firstId, long secondId) {
        return null;
    }

    @Override
    public User updateUser(User user) throws ValidationException {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean isContainId(long id) {
        return users.containsKey(id);
    }
}
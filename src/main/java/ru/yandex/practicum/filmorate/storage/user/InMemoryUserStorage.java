package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;


import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Long, User> users = new HashMap<>();
    private static long id = 1;

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
    public User updateUser(User user) throws ValidationException, NullPointerException {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean isContainId(long id) {
        return users.containsKey(id);
    }
}
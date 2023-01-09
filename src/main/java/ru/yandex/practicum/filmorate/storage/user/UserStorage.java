package ru.yandex.practicum.filmorate.storage.user;


import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    Map<Long, User> getUsers();
    User createUser(User user);
    User updateUser(User user);
    boolean isContainUser(User user);
}

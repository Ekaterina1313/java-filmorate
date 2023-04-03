package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DoesNotExistException;
import ru.yandex.practicum.filmorate.exception.UserIsAlreadyFriendException;
import ru.yandex.practicum.filmorate.exception.UsersAreNotFriendsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class  UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        log.debug("Текущее количество пользователей: {}", userStorage.getUsers().size());
        return new ArrayList<>(userStorage.getUsers().values());
    }

    public User createUser(User user) {
        if (isValid(user)) {
            if ((user.getName() == null) || (user.getName().isBlank())) {
                user.setName(user.getLogin());
            }
            log.debug("Добавлен новый пользователь: " + user.getName());
        }
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        if (userStorage.isContainId(user.getId())) {
            if (isValid(user)) {
                if ((user.getName() == null) || (user.getName().isBlank())) {
                    user.setName(user.getLogin());
                }
                log.debug("Обновлена информация о пользователе с id {}", user.getId());
            }
        } else {
            log.debug(" Пользователь с id {} не зарегистрирован.", user.getId());
            throw new DoesNotExistException("Пользователь с указанным id не зарегистрирован.");
        }
        return userStorage.updateUser(user);
    }

    public User getUserById(long id) {
        if (!userStorage.isContainId(id)) {
            log.debug(" Пользователь с id {} не зарегистрирован.", id);
            throw new DoesNotExistException("Пользователь с указанным id не зарегистрирован.");
        }
        return userStorage.getUserById(id);
    }

    public void addFriend(long firstId, long secondId) {
        isExist(firstId, secondId);
        if (isFriend(firstId, secondId)) {
            log.debug("Пользователь с id {} отправил запрос на добавление в друзья пользователю с id {}", firstId, secondId);
            throw new UserIsAlreadyFriendException("Пользователь уже отправил заявку.");
        } else {
            log.debug("Пользователи с id {} и {} теперь друзья!", firstId, secondId);
            userStorage.addFriend(firstId, secondId);
        }
    }

    public void deleteFriend(long firstId, long secondId) {
        isExist(firstId, secondId);
        if (isFriend(firstId, secondId)) {
            log.debug("Пользователь c id {} удалён из списка друзей пользователя с id {}", firstId, secondId);
            userStorage.deleteFriend(firstId, secondId);
        } else {
            log.debug("Пользователя c id {} нет в списке друзей у пользователя с id {}", firstId, secondId);
            throw new UsersAreNotFriendsException("Пользователя с id" + firstId +
                    " нет в друзьях у пользователя с id" + secondId);
        }
    }

    public List<Long> getAllFriends(long id) {
        if (!userStorage.isContainId(id)) {
            log.debug("Пользователь с указанным id {} не зарегистрирован.", id);
            throw new DoesNotExistException("Пользователь с id = " + id + "не зарегистрирован.");
        }
        return userStorage.getAllFriends(id); // метод, вызывающие список друзей
    }

    public List<User> getListOfFriendsById(List<Long> friends) {
        return userStorage.getListOfFriendsById(friends);
    }

    public List<Long> getListOfCommonFriends(long firstId, long secondId) {
        isExist(firstId, secondId);
        log.debug("Запрошен список общих друзей пользователей с id {} и {}", firstId, secondId);
        return userStorage.getListOfCommonFriends(firstId, secondId);
    }

    public boolean isFriend(long firstId, long secondId) {
        return userStorage.isFriend(firstId, secondId);
    }

    private boolean isValid(User user) {
        if (((user.getEmail() == null) || (user.getEmail().isBlank()))) {
            log.debug("Поле адреса электронной почты пользователя {} пустое", user.getName());
            throw new ValidationException("Адрес электронной почты не должен быть пустым.");
        } else if (!(user.getEmail().contains("@"))) {
            log.debug("Пользователем {} введён некорректный адрес электронной почты", user.getName());
            throw new ValidationException("Некорректный адрес электронной почты.");
        } else if ((user.getLogin() == null) || (user.getLogin().equals(""))) {
            log.debug("Неверно введён логин");
            throw new ValidationException("Логин не должен быть пустым.");
        } else if (user.getLogin().contains(" ")) {
            log.debug("Логин пользователя {} содержит пробелы", user.getName());
            throw new ValidationException("Логин не должен содержать пробелы.");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Похоже, пользователь {} из будущего! Указанная дата рождения: {}. Дата не должна быть раньше {}",
                    user.getName(), user.getBirthday(), LocalDate.now());
            throw new ValidationException("Дата рождения не может быть в будущем.");
        } else {
            return true;
        }
    }

    private boolean isExist(long id, long otherId) {
        if (!userStorage.isContainId(id)) {
            log.debug("Пользователь с указанным id {} не зарегистрирован.", id);
            throw new DoesNotExistException("Пользователь с id = " + id + "не зарегистрирован.");
        }
        if (!userStorage.isContainId(otherId)) {
            log.debug("Пользователь с указанным id {} не зарегистрирован.", otherId);
            throw new DoesNotExistException("Пользователь с id = " + otherId + " не зарегистрирован.");
        }
        return true;
    }
}
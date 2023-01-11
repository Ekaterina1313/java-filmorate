package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DoesNotExistException;
import ru.yandex.practicum.filmorate.exception.UserIsAlreadyFriendException;
import ru.yandex.practicum.filmorate.exception.UsersAreNotFriendsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping ({"/users"})
@Slf4j
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        log.debug("Текущее количество пользователей: {}", userStorage.getUsers().size());
        return new ArrayList<>(userStorage.getUsers().values());
    }

    @PostMapping
    public User create(@RequestBody User user) throws ValidationException {
        if (isValid(user)) {
            if ((user.getName() == null) || (user.getName().isBlank())) {
                user.setName(user.getLogin());
            }
            log.debug("Добавлен новый пользователь: " + user.getName());
        }
        return userStorage.createUser(user);
    }

    @PutMapping
    public User update(@RequestBody User user) throws ValidationException, NullPointerException {
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

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        if (!userStorage.isContainId(id)) {
            log.debug(" Пользователь с id {} не зарегистрирован.", id);
            throw new DoesNotExistException("Пользователь с указанным id не зарегистрирован.");
        }
        return userStorage.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) throws UserIsAlreadyFriendException {
        isExist(id, friendId);
        if (userService.isFriend(id, friendId)) {
            log.debug("Пользователь {} уже в друзьях у {}", userStorage.getUsers().get(id).getLogin(),
                    userStorage.getUsers().get(friendId).getLogin());
            throw new UserIsAlreadyFriendException("Пользователи уже друзья.");
        } else {
            log.debug("Пользователи {} и {} теперь друзья!", userStorage.getUsers().get(id).getLogin(),
                    userStorage.getUsers().get(friendId).getLogin());
            userService.addFriend(id, friendId);
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) throws UsersAreNotFriendsException {
        isExist(id, friendId);
        if (userService.isFriend(id, friendId)) {
            log.debug("Пользователь {} удалён из списка друзей пользователя {}", userStorage.getUsers().get(id).getLogin(),
                    userStorage.getUsers().get(friendId).getLogin());
            userService.deleteFriend(id, friendId);
        } else {
            log.debug("Пользователя {} нет в списке друзей у {}", userStorage.getUsers().get(id).getLogin(),
                    userStorage.getUsers().get(friendId).getLogin());
            throw new UsersAreNotFriendsException("Пользователя " + userStorage.getUsers().get(id).getLogin() +
                    " нет в друзьях у " + userStorage.getUsers().get(friendId).getLogin());
        }
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable long id) {
        if (!(userStorage.isContainId(id))) {
            log.debug("Пользователь с указанным id {} не зарегистрирован.", id);
            throw new DoesNotExistException("Пользователь с id = " + id + "не зарегистрирован.");
        }
        log.debug("Список всех друзей пользователя {} : {}.", userStorage.getUsers().get(id).getLogin(),
                userService.getListOfFriendsById(userStorage.getUsers().get(id).getListOfFriends()));
        return userService.getListOfFriendsById(userStorage.getUsers().get(id).getListOfFriends());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        isExist(id, otherId);
        log.debug("Список общих друзей пользователей {} и {}: {}", userStorage.getUsers().get(id).getLogin(),
                userStorage.getUsers().get(otherId).getLogin(), userService.getListOfCommonFriends(id, otherId));
        return userService.getListOfCommonFriends(id, otherId);
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
        if (!(userStorage.isContainId(id))) {
            log.debug("Пользователь с указанным id {} не зарегистрирован.", id);
            throw new DoesNotExistException("Пользователь с id = " + id + "не зарегистрирован.");
        }
        if (!(userStorage.isContainId(otherId))) {
            log.debug("Пользователь с указанным id {} не зарегистрирован.", otherId);
            throw new DoesNotExistException("Пользователь с id = " + otherId + " не зарегистрирован.");
        }
        return true;
    }
}
package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FileDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping ({"/users"})
@Slf4j
public class UserController {
    private final InMemoryUserStorage userStorage;

    @Autowired
    public UserController(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
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
        if (userStorage.isContainUser(user)) {
            if (isValid(user)) {
                if ((user.getName() == null) || (user.getName().isBlank())) {
                    user.setName(user.getLogin());
                }
                log.debug("Обновлена информация о пользователе с id {}", user.getId());
            }
        } else {
            log.debug(" Пользователь с id {} не существует.", user.getId());
            throw new FileDoesNotExistException("Пользователь с указанным id не существует.");
        }
        return userStorage.updateUser(user);
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
}
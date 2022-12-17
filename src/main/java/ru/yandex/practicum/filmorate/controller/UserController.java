package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping ({"/users"})

public class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private Map<Long, User> users = new HashMap<>();
    private static long id = 1;

    @GetMapping
    public User[] getUsers() { //противный постман все заваливал в этом методе и требовал список
        log.debug("Текущее количество пользователей: {}", users.size());
        return users.values().toArray(new User[0]);
    }

    public boolean isValid(User user) {
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
    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException {
        if (isValid(user)) {
            if ((user.getName() == null) || (user.getName().isBlank())) {
                user.setName(user.getLogin());
            }
            if (user.getId() == 0) { // условие на тот случай,когда придётся обновлять информацию о пользователе
                user.setId(id);
                id++;
                log.debug("Добавлен новый пользователь: " + user.getName());
            } else {
                log.debug("Обновлена информация о пользователе с id {}", user.getId());
            }
            users.put(user.getId(), user);
        }
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException, NullPointerException {
        if (users.containsKey(user.getId())) {
            createUser(user);
        } else {
            log.debug(" Пользователь с id {} не существует.", user.getId());
            throw new NullPointerException("Пользователь с указанным id не существует.");
        }
        return user;
    }
}









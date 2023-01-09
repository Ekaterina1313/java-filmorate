package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FileDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping ({"/users"})
@Slf4j
public class UserController {
    private Map<Long, User> users = new HashMap<>();
    private static long id = 1;

    @GetMapping
    public List<User> getUsers() { //противный постман все заваливал в этом методе и требовал список
        log.debug("Текущее количество пользователей: {}", users.size());
        List<User> listOfUsers = new ArrayList<>();
        listOfUsers.addAll(users.values());
        return listOfUsers;
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException {
        if (isValid(user)) {
            if ((user.getName() == null) || (user.getName().isBlank())) {
                user.setName(user.getLogin());
            }
            user.setId(id);
            id++;
            users.put(user.getId(), user);
            log.debug("Добавлен новый пользователь: " + user.getName());
        }
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException, NullPointerException {
        if (users.containsKey(user.getId())) {
            if (isValid(user)) {
                if ((user.getName() == null) || (user.getName().isBlank())) {
                    user.setName(user.getLogin());
                }
                log.debug("Обновлена информация о пользователе с id {}", user.getId());
                users.put(user.getId(), user);
            }
        } else {
            log.debug(" Пользователь с id {} не существует.", user.getId());
            throw new FileDoesNotExistException("Пользователь с указанным id не существует.");
        }
        return user;
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
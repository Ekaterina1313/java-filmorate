package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DoesNotExistException;
import ru.yandex.practicum.filmorate.exception.UserIsAlreadyFriendException;
import ru.yandex.practicum.filmorate.exception.UsersAreNotFriendsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
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
            log.debug("Пользователь {} уже в друзьях у {}", userStorage.getUsers().get(firstId).getLogin(),
                    userStorage.getUsers().get(secondId).getLogin());
            throw new UserIsAlreadyFriendException("Пользователи уже друзья.");
        } else {
            log.debug("Пользователи {} и {} теперь друзья!", userStorage.getUsers().get(firstId).getLogin(),
                    userStorage.getUsers().get(secondId).getLogin());
            User firstUser = userStorage.getUsers().get(firstId);
            User secondUser = userStorage.getUsers().get(secondId);
            firstUser.getListOfFriends().add(secondId);
            secondUser.getListOfFriends().add(firstId);
            userStorage.updateUser(firstUser);
            userStorage.updateUser(secondUser);
        }
    }

    public void deleteFriend(long firstId, long secondId) {
        isExist(firstId, secondId);
        if (isFriend(firstId, secondId)) {
            log.debug("Пользователь {} удалён из списка друзей пользователя {}", userStorage.getUsers().get(firstId).getLogin(),
                    userStorage.getUsers().get(secondId).getLogin());
            User firstUser = userStorage.getUsers().get(firstId);
            User secondUser = userStorage.getUsers().get(secondId);

            firstUser.getListOfFriends().remove(secondId);
            secondUser.getListOfFriends().remove(firstId);
            userStorage.updateUser(firstUser);
            userStorage.updateUser(secondUser);
        } else {
            log.debug("Пользователя {} нет в списке друзей у {}", userStorage.getUsers().get(firstId).getLogin(),
                    userStorage.getUsers().get(secondId).getLogin());
            throw new UsersAreNotFriendsException("Пользователя " + userStorage.getUsers().get(firstId).getLogin() +
                    " нет в друзьях у " + userStorage.getUsers().get(secondId).getLogin());
        }
    }

    public List<User> getAllFriends(long id) {
        if (!userStorage.isContainId(id)) {
            log.debug("Пользователь с указанным id {} не зарегистрирован.", id);
            throw new DoesNotExistException("Пользователь с id = " + id + "не зарегистрирован.");
        }
        log.debug("Список всех друзей пользователя {} : {}.", userStorage.getUsers().get(id).getLogin(),
                getListOfFriendsById(userStorage.getUsers().get(id).getListOfFriends()));
        return getListOfFriendsById(userStorage.getUserById(id).getListOfFriends());
    }

    public List<User> getListOfFriendsById(Set<Long> friends) {
        List<User> listOfFriends = new ArrayList<>();
        for (Long id : friends) {
            listOfFriends.add(userStorage.getUsers().get(id));
        }
        return listOfFriends;
    }

    public List<User> getListOfCommonFriends(long firstId, long secondId) {
        isExist(firstId, secondId);
        log.debug("Запрошен список общих друзей пользователей {} и {}", userStorage.getUsers().get(firstId).getLogin(),
                userStorage.getUsers().get(secondId).getLogin());
        Set<Long> listOfCommonFriends = new HashSet<>();
        Set<Long> firstIdFriends = userStorage.getUsers().get(firstId).getListOfFriends();
        Set<Long> secondIdFriends = userStorage.getUsers().get(secondId).getListOfFriends();
        for (Long id : firstIdFriends) {
            if (secondIdFriends.contains(id)) {
                listOfCommonFriends.add(id);
            }
        }
        return getListOfFriendsById(listOfCommonFriends);
    }

    public boolean isFriend(long firstId, long secondId) {
        return userStorage.getUsers().get(firstId).getListOfFriends().contains(secondId);
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
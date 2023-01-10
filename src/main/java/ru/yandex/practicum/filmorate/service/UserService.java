package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
private final UserStorage userStorage;

@Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(long firstId, long secondId) {
        User firstUser = userStorage.getUsers().get(firstId);
        User secondUser = userStorage.getUsers().get(secondId);
        firstUser.getListOfFriends().add(secondId);
        secondUser.getListOfFriends().add(firstId);
        userStorage.updateUser(firstUser);
        userStorage.updateUser(secondUser);
    }

    public void deleteFriend(long firstId, long secondId) {
        User firstUser = userStorage.getUsers().get(firstId);
        User secondUser = userStorage.getUsers().get(secondId);

        firstUser.getListOfFriends().remove(secondId);
        secondUser.getListOfFriends().remove(firstId);
        userStorage.updateUser(firstUser);
        userStorage.updateUser(secondUser);
    }

    public List<User> getListOfFriendsById(Set<Long> friends) {
    List<User> listOfFriends = new ArrayList<>();
    for (Long id : friends) {
        listOfFriends.add(userStorage.getUsers().get(id));
    }
    return listOfFriends;
    }

    public List<User> getListOfCommonFriends(long firstId, long secondId) {
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
}
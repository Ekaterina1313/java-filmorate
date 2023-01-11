package ru.yandex.practicum.filmorate.testUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.DoesNotExistException;
import ru.yandex.practicum.filmorate.exception.UserIsAlreadyFriendException;
import ru.yandex.practicum.filmorate.exception.UsersAreNotFriendsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
   private UserController controller;

    User testUser;
    @BeforeEach
    public void beforeEach() {
        InMemoryUserStorage storage = new InMemoryUserStorage();
        controller = new UserController(storage, new UserService(storage));
        testUser = User.builder().email("mirasolar@mail.ru")
                .login("Mira-Mira")
                .birthday(LocalDate.of(2000,12,12))
                .name("Mira")
                .build();
    }

    // GET
    @Test
    public void getListOfUsers() throws ValidationException {
        List<User> testFilmsArray = new ArrayList<>();
        assertEquals(controller.getUsers(), testFilmsArray);

        controller.create(testUser);
        testFilmsArray.add(testUser);
        assertEquals(controller.getUsers(), testFilmsArray);
    }

    //POST
    @Test
    public void testCreateUserWithEmptyEmail() {
        testUser.setEmail("");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.create(testUser));
        assertEquals("Адрес электронной почты не должен быть пустым.", exception.getMessage());
    }

    @Test
    public void testCreateEmailWithoutSymbol() {
        testUser.setEmail("mirasolarmail.ru");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.create(testUser));
        assertEquals("Некорректный адрес электронной почты.", exception.getMessage());
    }

    @Test
    public void testCreateUserWithEmptyLogin() {
        testUser.setLogin("");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.create(testUser));
        assertEquals("Логин не должен быть пустым.", exception.getMessage());
    }

    @Test
    public void testCreateLoginWithBlank() {
        testUser.setLogin("Mira 11");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.create(testUser));
        assertEquals("Логин не должен содержать пробелы.", exception.getMessage());
    }

    @Test
    public void testCreateUserWithIncorrectBirthday() {
        testUser.setBirthday(LocalDate.of(2100,11,11));
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.create(testUser));
        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
    }

    //PUT
    @Test
    public void testUpdateUserWithIncorrectId() throws ValidationException {
        controller.create(testUser);
        User testUser2 = User.builder().email("mirasolar@mail.ru")
                .login("Mrmr")
                .birthday(LocalDate.of(1990,10,10))
                .name("Toruviel")
                .id(3000)
                .build();

        DoesNotExistException exception = assertThrows(DoesNotExistException.class, () -> controller.update(testUser2));
        assertEquals("Пользователь с указанным id не зарегистрирован.", exception.getMessage());
    }

    @Test
    public void testUpdateUser() throws ValidationException {
        controller.create(testUser);
        User testUser2 = User.builder().email("mirasolar@mail.ru")
                .login("Mrmr")
                .birthday(LocalDate.of(1990,10,10))
                .name("")
                .id(testUser.getId())
                .build();
        controller.update(testUser2);
        List<User> testUsers = new ArrayList<>();
        testUsers.add(testUser2);

        assertEquals(testUsers, controller.getUsers());
    }

    //others

    @Test
    public void testGetUser() {
        DoesNotExistException exception = assertThrows(DoesNotExistException.class, () ->controller.getUser(8));
        assertEquals("Пользователь с указанным id не зарегистрирован.", exception.getMessage());

        controller.create(testUser);
        assertEquals(testUser, controller.getUser(testUser.getId()));
    }

    @Test
    public void testAddFriend() throws UserIsAlreadyFriendException {
        controller.create(testUser);
        User testUser2 = User.builder().email("mirasolar@mail.ru")
                .login("Mrmr")
                .birthday(LocalDate.of(1990,10,10))
                .name("")
                .id(testUser.getId())
                .build();
        controller.create(testUser2);
        controller.addFriend(testUser.getId(), testUser2.getId());
        Set<Long> testListOfFriends = new HashSet<>();
        testListOfFriends.add(testUser2.getId());
        assertEquals(controller.getUser(testUser.getId()).getListOfFriends(), testListOfFriends);
    }

    @Test
    public void testDeleteFriend() throws UserIsAlreadyFriendException, UsersAreNotFriendsException {
        controller.create(testUser);
        User testUser2 = User.builder().email("mirasolar@mail.ru")
                .login("Mrmr")
                .birthday(LocalDate.of(1990,10,10))
                .name("")
                .id(testUser.getId())
                .build();
        controller.create(testUser2);
        controller.addFriend(testUser.getId(), testUser2.getId());
        controller.deleteFriend(testUser2.getId(), testUser.getId());
        Set<Long> testListOfFriends = new HashSet<>();
        assertEquals(testListOfFriends, testUser.getListOfFriends());

        UsersAreNotFriendsException exception = assertThrows(UsersAreNotFriendsException.class,
                () ->controller.deleteFriend(testUser.getId(), testUser2.getId()));
        assertEquals("Пользователя Mira-Mira нет в друзьях у Mrmr", exception.getMessage());
    }

    @Test
    public void testGetAllFriendList() throws UserIsAlreadyFriendException {
        controller.create(testUser);
        User testUser2 = User.builder().email("mirasolar@mail.ru")
                .login("Mrmr")
                .birthday(LocalDate.of(1990,10,10))
                .name("")
                .id(testUser.getId())
                .build();
        controller.create(testUser2);
        List<User> testList = new ArrayList<>();
        assertEquals(testList, controller.getAllFriends(testUser.getId()));

        controller.addFriend(testUser.getId(), testUser2.getId());
        testList.add(testUser);
        assertEquals(testList, controller.getAllFriends(testUser2.getId()));
    }

    @Test
    public void testGetCommonFriends() throws UserIsAlreadyFriendException {
        controller.create(testUser);
        User testUser2 = User.builder().email("mirasolar@mail.ru")
                .login("Mrmr")
                .birthday(LocalDate.of(1990,10,10))
                .name("")
                .id(testUser.getId())
                .build();
        User testUser3 = User.builder().email("mirasolar@mail.ru")
                .login("Mrmr")
                .birthday(LocalDate.of(1990,10,10))
                .name("")
                .id(testUser.getId())
                .build();
        controller.create(testUser2);
        controller.create(testUser3);
        List<User> testListOfCommonFriends = new ArrayList<>();
        assertEquals(testListOfCommonFriends, controller.getCommonFriends(testUser.getId(),testUser2.getId()));

        controller.addFriend(testUser.getId(), testUser2.getId());
        controller.addFriend(testUser2.getId(), testUser3.getId());
        testListOfCommonFriends.add(testUser2);
        assertEquals(testListOfCommonFriends, controller.getCommonFriends(testUser.getId(),testUser3.getId()));
    }

}
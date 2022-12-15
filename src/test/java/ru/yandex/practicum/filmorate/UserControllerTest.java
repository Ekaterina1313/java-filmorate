package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import static org.junit.jupiter.api.Assertions.*;


import static org.junit.jupiter.api.Assertions.assertEquals;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class UserControllerTest {
    UserController controller;
    User testUser;
    @BeforeEach
    public void beforeEach() {
        controller = new UserController();
        testUser = User.builder().email("mirasolar@mail.ru")
                .login("Mira-Mira")
                .birthday(LocalDate.of(2000,12,12))
                .name("Mira")
                .build();
    }

    // GET
    @Test
    public void getListOfUsers() throws ValidationException {
        Set<User> testUsers = new HashSet<>();
        assertEquals(controller.getUsers(), testUsers);

        testUsers.add(testUser);
        controller.createUser(testUser);
        assertEquals(controller.getUsers(), testUsers);

    }

    //POST
    @Test
    public void testCreateUserWithEmptyEmail() {
        testUser.setEmail("");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(testUser));
        assertEquals("Адрес электронной почты не должен быть пустым.", exception.getMessage());
    }

    @Test
    public void testCreateEmailWithoutSymbol() {
        testUser.setEmail("mirasolarmail.ru");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(testUser));
        assertEquals("Некорректный адрес электронной почты.", exception.getMessage());
    }

    @Test
    public void testCreateUserWithEmptyLogin() {
        testUser.setLogin("");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(testUser));
        assertEquals("Логин не должен быть пустым.", exception.getMessage());
    }

    @Test
    public void testCreateLoginWithBlank() {
        testUser.setLogin("Mira 11");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(testUser));
        assertEquals("Логин не должен содержать пробелы.", exception.getMessage());
    }

    @Test
    public void testCreateUserWithIncorrectBirthday() {
        testUser.setBirthday(LocalDate.of(2100,11,11));
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(testUser));
        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
    }

    //PUT
    @Test
    public void testUpdateUserWithIncorrectId() throws ValidationException {
        controller.createUser(testUser);

        User testUser2 = User.builder().email("mirasolar@mail.ru")
                .login("Mrmr")
                .birthday(LocalDate.of(1990,10,10))
                .name("Toruviel")
                .id(3000)
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.updateUser(testUser2));
        assertEquals("Пользователь с указанным id не существует.", exception.getMessage());
    }

    @Test
    public void testUpdateUser() throws ValidationException {
        controller.createUser(testUser);
        User testUser2 = User.builder().email("mirasolar@mail.ru")
                .login("Mrmr")
                .birthday(LocalDate.of(1990,10,10))
                .name("")
                .id(testUser.getId())
                .build();
        controller.updateUser(testUser2);
        Set<User> testUsers = new HashSet<>();
        testUsers.add(testUser2);

        assertEquals(testUsers, controller.getUsers());
    }

}

package ru.yandex.practicum.filmorate.testUser;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserIsAlreadyFriendException;
import ru.yandex.practicum.filmorate.exception.UsersAreNotFriendsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.friends.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {
    UserController controller;
    User testUser1;
    User testUser2;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void beforeEach() {
        controller = new UserController(new UserService(new UserDbStorage(jdbcTemplate), new FriendsDbStorage(jdbcTemplate)));

        jdbcTemplate.execute("delete from friendship");
        jdbcTemplate.execute("DELETE FROM users");

        testUser1 = new User(1, "Nana", "lunar", LocalDate.of(1990, 12, 12),
                "nana@mail.ru");
        testUser2 = new User(2, "Mira", "Mira", LocalDate.of(1995, 11, 11),
                "mira@mail.ru");
    }

    @Test
    public void testGetUserById() {
        controller.createUser(testUser1);

        Optional<User> userOptional = Optional.ofNullable(controller.getUserById(testUser1.getId()));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", testUser1.getId())
                );
    }

    @Test
    public void testCreateUser() {
        User createdUser = controller.createUser(testUser1);

        assertThat(createdUser.getId()).isPositive();

        String sql = "select * from users where id = ?";
        User retrievedUser = jdbcTemplate.queryForObject(sql, new Object[]{createdUser.getId()}, new UserRowMapper());

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getName()).isEqualTo(testUser1.getName());
        assertThat(retrievedUser.getLogin()).isEqualTo(testUser1.getLogin());
        assertThat(retrievedUser.getEmail()).isEqualTo(testUser1.getEmail());
        assertThat(retrievedUser.getBirthday()).isEqualTo(testUser1.getBirthday());
    }

    // get
    @Test
    public void testGetAllUsers() {
        controller.createUser(testUser1);
        controller.createUser(testUser2);
        List<User> allUsers = controller.getUsers();

        assertThat(allUsers).contains(testUser1, testUser2);
    }

    @Test
    public void testUpdateUser() {
        controller.createUser(testUser1);

        testUser1.setName("UpdatedTestUser1");
        testUser1.setLogin("updatedTestUser1");
        testUser1.setEmail("updatedTestUser1@example.com");
        testUser1.setBirthday(LocalDate.of(2000, 2, 2));
        controller.updateUser(testUser1);

        User updatedUser = controller.getUserById(testUser1.getId());

        assertThat(updatedUser)
                .isEqualTo(testUser1)
                .hasFieldOrPropertyWithValue("name", "UpdatedTestUser1")
                .hasFieldOrPropertyWithValue("login", "updatedTestUser1")
                .hasFieldOrPropertyWithValue("email", "updatedTestUser1@example.com")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 2, 2));
    }

    @Test
    public void testAddFriend() {
        controller.createUser(testUser1);
        controller.createUser(testUser2);
        long userId = testUser1.getId();
        long friendId = testUser2.getId();

        controller.addFriend(userId, friendId);

        SqlRowSet result = jdbcTemplate.queryForRowSet("select * from friendship where user_id = ? and friend_id = ?", userId, friendId);
        assertTrue(result.next());
    }

    @Test
    public void testDeleteFriend() {
        controller.createUser(testUser1);
        controller.createUser(testUser2);
        long userId = testUser1.getId();
        long friendId = testUser2.getId();
        controller.addFriend(userId, friendId);
        controller.deleteFriend(userId, friendId);

        SqlRowSet result = jdbcTemplate.queryForRowSet("select * from friendship where user_id = ? and friend_id = ?", userId, friendId);
        assertFalse(result.next());
    }

    @Test
    public void testGetAllFriends() {
        controller.createUser(testUser1);
        controller.createUser(testUser2);
        long userId = testUser1.getId();
        long friendId = testUser2.getId();

        controller.addFriend(userId, friendId);
        List<User> expectedList = controller.getAllFriends(userId);
        assertEquals(1, expectedList.size());
        assertEquals(testUser2, expectedList.get(0));
    }

    @Test
    public void testGetListOfCommonFriends() {
        User testUser3 = new User(3, "Lolo", "ololo", LocalDate.of(1999, 10, 16),
                "ololosha@mail.ru");
        controller.createUser(testUser1);
        controller.createUser(testUser2);
        controller.createUser(testUser3);

        controller.addFriend(testUser1.getId(), testUser2.getId());
        controller.addFriend(testUser1.getId(), testUser3.getId());
        controller.addFriend(testUser2.getId(), testUser3.getId());
    }

    @Test
    public void testUserNotNull() {
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(null));
        assertEquals("User не может быть null.", exception.getMessage());
    }

    @Test
    public void testCreateUserWithEmptyEmail() {
        testUser1.setEmail("");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(testUser1));
        assertEquals("Адрес электронной почты не должен быть пустым.", exception.getMessage());
    }

    @Test
    public void testCreateEmailWithoutSymbol() {
        testUser1.setEmail("mirasolarmail.ru");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(testUser1));
        assertEquals("Некорректный адрес электронной почты.", exception.getMessage());
    }

    @Test
    public void testCreateUserWithEmptyLogin() {
        testUser1.setLogin("");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(testUser1));
        assertEquals("Логин не должен быть пустым.", exception.getMessage());
    }

    @Test
    public void testCreateLoginWithBlank() {
        testUser1.setLogin("Mira 11");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(testUser1));
        assertEquals("Логин не должен содержать пробелы.", exception.getMessage());
    }

    @Test
    public void testCreateUserWithIncorrectBirthday() {
        testUser1.setBirthday(LocalDate.of(2100, 11, 11));
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(testUser1));
        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
    }

    //
    @Test
    public void testUpdateUserWithIncorrectId() throws RuntimeException {
        User testUser3 = new User(1000000, "Mira", "Mira", LocalDate.of(1995, 11, 11),
                "mira@mail.ru");
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> controller.updateUser(testUser3));
        assertEquals("Пользователь с указанным id не зарегистрирован.", exception.getMessage());
    }

    @Test
    public void testUserIsAlreadyFriendException() {
        controller.createUser(testUser1);
        controller.createUser(testUser2);
        long userId = testUser1.getId();
        long friendId = testUser2.getId();
        controller.addFriend(userId, friendId);
        UserIsAlreadyFriendException exception = assertThrows(UserIsAlreadyFriendException.class, () -> controller.addFriend(userId, friendId));
        assertEquals("Пользователь уже в друзьях.", exception.getMessage());
    }

    @Test
    public void testUsersAreNotFriendsException() {
        controller.createUser(testUser1);
        controller.createUser(testUser2);
        long userId = testUser1.getId();
        long friendId = testUser2.getId();
        UsersAreNotFriendsException exception = assertThrows(UsersAreNotFriendsException.class, () -> controller.deleteFriend(userId, friendId));
        assertEquals("Этого пользователя нет в списке друзей.", exception.getMessage());
    }
}
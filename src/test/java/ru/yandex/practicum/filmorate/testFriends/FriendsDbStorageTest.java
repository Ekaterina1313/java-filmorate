package ru.yandex.practicum.filmorate.testFriends;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendsDbStorageTest {

    User testUser1;
    User testUser2;
    @Autowired
    private UserDbStorage userStorage;
    @Autowired
    private FriendsDbStorage friendsDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void beforeEach() {
        jdbcTemplate.execute("delete from friendship");
        jdbcTemplate.execute("delete from users");
        testUser1 = new User(1, "Nana", "lunar", LocalDate.of(1990, 12, 12),
                "nana@mail.ru");
        testUser2 = new User(2, "Mira", "Mira", LocalDate.of(1995, 11, 11),
                "mira@mail.ru");
        userStorage.createUser(testUser1);
        userStorage.createUser(testUser2);
    }

    @Test
    public void testIsFriend() {
        long userId = testUser1.getId();
        long friendId = testUser2.getId();

        assertFalse(friendsDbStorage.isFriend(userId, friendId));

        jdbcTemplate.update("insert into friendship (user_id, friend_id) values (?, ?)", userId, friendId);
        assertTrue(friendsDbStorage.isFriend(userId, friendId));
    }
}
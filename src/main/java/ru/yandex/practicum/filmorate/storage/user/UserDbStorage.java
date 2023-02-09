package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    private long id = 1;
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, User> getUsers() {
        Map<Long, User> sqlUsers = new HashMap<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from user");
        while (userRows.next()) {
            User user = new User(
                    userRows.getLong("id"),
                    userRows.getString("name"),
                    userRows.getString("login"),
                    userRows.getDate("birthday").toLocalDate(),
                    userRows.getString("email"));
            sqlUsers.put(user.getId(),user);
        }
        return sqlUsers;
    }

    @Override
    public User createUser(User user) {
        user.setId(id);
        id++;
        String sqlQuery = "insert into user(id, name, login, birthday, email) " +
                "values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                user.getId(),
                user.getName(),
                user.getLogin(),
                user.getBirthday(),
                user.getEmail());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "update user set" + "(name = ?, login = ?, birthday = ?, email = ?)" + "where id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getLogin(),
                user.getBirthday(),
                user.getEmail(),
                user.getId());
        return user;
    }

    @Override
    public boolean isContainId(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from user where id = ?", id);
        return userRows.next();
    }

    @Override
    public User getUserById(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from user where id = ?", id);
        User user = new User(
                userRows.getLong("id"),
                userRows.getString("name"),
                userRows.getString("login"),
                userRows.getDate("birthday").toLocalDate(),
                userRows.getString("email"));
        return user;
    }
}

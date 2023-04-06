package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, User> getUsers() {
        Map<Long, User> sqlUsers = new HashMap<>();

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users");
        while (userRows.next()) {
            User user = new User(userRows.getLong("id"),
                    userRows.getString("name"),
                    userRows.getString("login"),
                    Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate(),
                    userRows.getString("email"));
            sqlUsers.put(user.getId(), user);
        }
        return sqlUsers;
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("insert into users (name, login, birthday, email) " +
                    "values (?, ?, ?, ?)", new String[]{"id"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getLogin());
            ps.setDate(3, Date.valueOf(user.getBirthday()));
            ps.setString(4, user.getEmail());
            return ps;
        }, keyHolder);
        long id = keyHolder.getKey().longValue();
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "update users set" + " name = ?, login = ?, birthday = ?, email = ?" + "where id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getLogin(),
                user.getBirthday(),
                user.getEmail(),
                user.getId());
        return user;
    }

    @Override
    public User getUserById(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where id = ?", id);
        userRows.next();
        User user = new User(
                userRows.getLong("id"),
                userRows.getString("name"),
                userRows.getString("login"),
                Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate(),
                userRows.getString("email"));
        return user;
    }

    @Override
    public boolean isContainId(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where id = ?", id);
        return userRows.next();
    }
}
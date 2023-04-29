package ru.yandex.practicum.filmorate.storage.friends;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sqlQuery = "insert into friendship (user_id, friend_id)" +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        String sqlQuery = "delete from friendship " +
                "where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> getAllFriends(long id) {
        List<User> friendsOfUser = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users as U, friendship as F " +
                "where U.id = F.friend_id AND F.user_id = ?", id);
        while (userRows.next()) {
            User user = new User(userRows.getLong("id"),
                    userRows.getString("name"),
                    userRows.getString("login"),
                    userRows.getDate("birthday").toLocalDate(),
                    userRows.getString("email"));
            friendsOfUser.add(user);
        }
        return friendsOfUser;
    }

    @Override
    public List<User> getListOfCommonFriends(long firstId, long secondId) {
        List<User> listOfCommonFriends = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users as U, friendship as F, friendship as FR " +
                "where U.id = F.friend_id and U.id = FR.friend_id and F.user_id = ? and FR.user_id = ?", firstId, secondId);
        while (userRows.next()) {
            listOfCommonFriends.add(new User(userRows.getLong("id"),
                    userRows.getString("name"),
                    userRows.getString("login"),
                    userRows.getDate("birthday").toLocalDate(),
                    userRows.getString("email")));
        }
        return listOfCommonFriends;
    }

    @Override
    public boolean isFriend(long userId, long friendId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendship where user_id = ? and friend_id = ?", userId, friendId);
        return userRows.next();
    }
}
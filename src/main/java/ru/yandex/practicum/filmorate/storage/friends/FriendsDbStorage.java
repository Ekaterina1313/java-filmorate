package ru.yandex.practicum.filmorate.storage.friends;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Component
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    public FriendsDbStorage(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
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
        String sql = ("select * from users as U, friendship as F " +
                "where U.id = F.friend_id AND F.user_id = ?");
        List<User> friendsOfUser = jdbcTemplate.query(sql, userRowMapper, id);
        return friendsOfUser;
    }

    @Override
    public List<User> getListOfCommonFriends(long firstId, long secondId) {
        String sql = ("select * from users as U, friendship as F, friendship as FR " +
                "where U.id = F.friend_id and U.id = FR.friend_id and F.user_id = ? and FR.user_id = ?");
        Object[] args = new Object[]{firstId, secondId};
        int[] argTypes = new int[]{Types.BIGINT, Types.BIGINT};
        List<User> users = jdbcTemplate.query(sql, args, argTypes, userRowMapper);
        List<User> listOfCommonFriends = new ArrayList<>();
        listOfCommonFriends.addAll(users);
        return listOfCommonFriends;
    }

    @Override
    public boolean isFriend(long userId, long friendId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendship where user_id = ? and friend_id = ?", userId, friendId);
        return userRows.next();
    }
}
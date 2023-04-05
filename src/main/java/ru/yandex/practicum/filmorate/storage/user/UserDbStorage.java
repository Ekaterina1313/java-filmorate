package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Component
public class UserDbStorage implements UserStorage {
    //private long id = 1;
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, User> getUsers() {
        Map<Long, User> sqlUsers = new HashMap<>();

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users");
        SqlRowSet friendshipRows = jdbcTemplate.queryForRowSet("select * from friendship");
        while (userRows.next()) {
            User user = new User(userRows.getLong("id"),
                    userRows.getString("name"),
                    userRows.getString("login"),
                    Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate(),
                    userRows.getString("email"));
            sqlUsers.put(user.getId(), user);
        }
        while (friendshipRows.next()) {
            if (friendshipRows.getString("status").equals("CONFIRMED")) {
                sqlUsers.get(friendshipRows.getLong("user_id")).getFriendshipStatusMap()
                                .put(friendshipRows.getLong("friend_id"), FriendshipStatus.CONFIRMED);
            } else {
                sqlUsers.get(friendshipRows.getLong("user_id")).getFriendshipStatusMap()
                                .put(friendshipRows.getLong("friend_id"), FriendshipStatus.UNCONFIRMED);
            }
        }
        return sqlUsers;
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("insert into users (name, login, birthday, email) " +
                    "values (?, ?, ?, ?)", new String[] {"id"});
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
        SqlRowSet friendshipRows = jdbcTemplate.queryForRowSet("select * from friendship where user_id = ?", id);
        while (friendshipRows.next()) {
            if (friendshipRows.getString("status").equals("CONFIRMED")) {
                user.getFriendshipStatusMap().put(friendshipRows.getLong("friend_id"), FriendshipStatus.CONFIRMED);
            } else {
                user.getFriendshipStatusMap().put(friendshipRows.getLong("friend_id"), FriendshipStatus.UNCONFIRMED);
            }
        }
        return user;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendship where user_id = ? and friend_id = ?", friendId, userId);
        if (userRows.next()) {
            String sqlQuery = "update friendship set status = ? where friendship_id = ?";
            jdbcTemplate.update(sqlQuery, "CONFIRMED", userRows.getLong("friendship_id"));

            sqlQuery = "insert into friendship (user_id, friend_id, status)" +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sqlQuery, userId, friendId, "CONFIRMED");

        } else {
            String sqlQuery = "insert into friendship (user_id, friend_id, status)" +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sqlQuery, userId, friendId, "UNCONFIRMED");
        }
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendship where user_id = ? and friend_id = ?", friendId, userId);
        if (userRows.next()) {
            String sqlQuery = "update friendship set " +
                    "status = ?" +
                    "where friendship_id = ?";
            jdbcTemplate.update(sqlQuery,
                    "UNCONFIRMED",
                    userRows.getLong("friendship_id"));
        }
        String sqlQuery = "delete from friendship " +
                "where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> getAllFriends(long id) {
        List<Long> friendsOfUser = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendship where user_id = ?", id);
        while (userRows.next()) {
            long friendId = userRows.getLong("friend_id");
            friendsOfUser.add(friendId);
        }
        return getListOfFriendsById(friendsOfUser);
    }

    @Override
    public List<User> getListOfFriendsById(List<Long> friends) {
        List<User> friendOfUserById = new ArrayList<>();
        Map<Long, User> mapOfAllUsers = getUsers();
        for (Long userId : friends) {
            friendOfUserById.add(mapOfAllUsers.get(userId));
        }
        return friendOfUserById;
    }

    @Override
    public List<User> getListOfCommonFriends(long firstId, long secondId) {
        List<User> listOfCommonFriends = new ArrayList<>();
        Map<Long, User> mapOfUsers = getUsers();
        Map<Long, FriendshipStatus> firstUserFriends = mapOfUsers.get(firstId).getFriendshipStatusMap();
        Map<Long, FriendshipStatus> secondUserFriends = mapOfUsers.get(secondId).getFriendshipStatusMap();
        List<Long> idListOfCommonFriends = new ArrayList<>();
        for (Long element : firstUserFriends.keySet()) {
            if (secondUserFriends.containsKey(element)) {
                idListOfCommonFriends.add(element);
            }
        }
        for (Long element : idListOfCommonFriends) {
            listOfCommonFriends.add(mapOfUsers.get(element));
        }
        return listOfCommonFriends;
    }

    @Override
    public boolean isContainId(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where id = ?", id);
        return userRows.next();
    }

    @Override
    public boolean isFriend(long userId, long friendId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendship where user_id = ? and friend_id = ?", userId, friendId);
        return userRows.next();
    }
}
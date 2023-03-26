package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    private long id = 4;
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, User> getUsers() {
        Map<Long, User> sqlUsers = new HashMap<>();
        User user = new User(0, null, null, null, null);
        Map<Long, FriendshipStatus> friendship = new HashMap<>();

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from user as u " +
                "left outer join friendship as f on u.id = f.friend_id" +
                "order by id asc");
        long firstId = 1;
        while (userRows.next()) {
            if (userRows.getLong("id") != firstId) { // условие выполнится, когда появится строка с ид следующего юзера
            user.setFriendshipStatusMap(friendship);
            sqlUsers.put(user.getId(), user);
            firstId = userRows.getLong("id");
            friendship.clear();
            }
            if (userRows.getObject("user_id", Long.class) != null) { // заполним хэшмапу friendship
                friendship.put(userRows.getLong("user_id"), userRows.getObject("status", FriendshipStatus.class));
            }
            user.setId(userRows.getLong("id"));
            user.setName(userRows.getString("name"));
            user.setLogin(userRows.getString("login"));
            user.setBirthday(userRows.getDate("birthday").toLocalDate());
            user.setEmail(userRows.getString("email"));
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

    @Override
    public void addFriend(long from, long to) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendship where from_id = ? and to_id = ?", to, from);
        if (userRows.next()) {
            String sqlQuery = "insert into friendship (from_id, to_id, status)" +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sqlQuery, from, to, FriendshipStatus.CONFIRMED);
            sqlQuery = "update friendship set status = ? where friendship_id = ?";
            jdbcTemplate.update(sqlQuery, FriendshipStatus.CONFIRMED, userRows.getLong("friendship_id"));
        } else {
            String sqlQuery = "insert into friendship (from_id, to_id, status)" +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sqlQuery, from, to, FriendshipStatus.UNCONFIRMED);
        }
    }

    @Override
    public void deleteFriend(long from, long to) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendship where from_id = ? and to_id = ?", to, from);
        if (userRows.next()) {
            String sqlQuery = "update friendship set status = ? where friendship_id = ?";
            jdbcTemplate.update(sqlQuery, FriendshipStatus.UNCONFIRMED, userRows.getLong("friendship_id"));
        }
        String sqlQuery = ("delete from friendship where friendship_id = ?");
        jdbcTemplate.update(sqlQuery, userRows.getLong("friendship_id")); // можно удалить по входящим переменным from, to
    }

    @Override
    public List<Long> getAllFriends(long id) {
        List<Long> friendsOfUser = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendship where friend_id = ?", id);
        if (userRows.next()) {
           Long friendId = userRows.getLong("user_id");
            friendsOfUser.add(friendId);
        }
        return friendsOfUser;
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
    public List<Long> getListOfCommonFriends(long firstId, long secondId) {
        List<Long> friendsOfFirstUser = getAllFriends(firstId);
        List<Long> friendsOfSecondUser = getAllFriends(secondId);
        List<Long> listOfCommonFriends = new ArrayList<>();
        for (Long friendId : friendsOfFirstUser) {
            if (friendsOfSecondUser.contains(friendId)) {
                listOfCommonFriends.add(friendId);
            }
        }
        return listOfCommonFriends;
    }

    @Override
    public boolean isContainId(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from user where id = ?", id);
        return userRows.next();
    }

    @Override
    public boolean isFriend (long first_id, long second_id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friendship where from_id = ? and to_id = ?", first_id, second_id);
        return userRows.next();
    }
}
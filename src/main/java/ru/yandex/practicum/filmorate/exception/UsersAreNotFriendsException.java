package ru.yandex.practicum.filmorate.exception;

public class UsersAreNotFriendsException extends Exception {
    public UsersAreNotFriendsException(String message) {
        super(message);
    }
}
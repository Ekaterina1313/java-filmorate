package ru.yandex.practicum.filmorate.exception;

public class UserIsAlreadyFriendException extends Exception {
    public UserIsAlreadyFriendException(String message) {
        super(message);
    }
}
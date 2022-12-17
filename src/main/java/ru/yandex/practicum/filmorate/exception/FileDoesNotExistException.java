package ru.yandex.practicum.filmorate.exception;

public class FileDoesNotExistException extends RuntimeException {
    public FileDoesNotExistException(String message) {
        super(message);
    }
}

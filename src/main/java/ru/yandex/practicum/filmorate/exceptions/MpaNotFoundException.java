package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MpaNotFoundException extends NotFoundException {
    public MpaNotFoundException(String message) {
        super(message);
    }
}

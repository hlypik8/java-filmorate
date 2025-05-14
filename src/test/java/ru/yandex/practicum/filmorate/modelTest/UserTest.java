package ru.yandex.practicum.filmorate.modelTest;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private Validator validator;
    private User user;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        user = new User();
        user.setEmail("test@example.com");
        user.setLogin("validLogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void shouldCreateValidUser() {
        assertTrue(validator.validate(user).isEmpty());
    }

    @Test
    void shouldUseLoginAsNameWhenNameIsEmpty() {
        user.setName("");
        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void shouldFailWhenEmailIsInvalid() {
        user.setEmail("invalid-email");
        assertEquals(1, validator.validate(user).size());
    }

    @Test
    void shouldFailWhenLoginHasSpaces() {
        user.setLogin("invalid login");
        assertEquals(1, validator.validate(user).size());
    }

    @Test
    void shouldSetIdCorrectly() {
        user.setId(10);
        assertEquals(10, user.getId());
    }
}

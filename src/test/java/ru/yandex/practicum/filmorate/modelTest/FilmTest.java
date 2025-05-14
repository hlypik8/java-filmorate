package ru.yandex.practicum.filmorate.modelTest;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {
    private Validator validator;
    private Film film = new Film();

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        film = new Film();
        film.setName("Valid Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(Duration.ofMinutes(120));
    }

    @Test
    void shouldCreateValidFilm() {
        assertTrue(validator.validate(film).isEmpty());
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        film.setName(" ");
        assertEquals(1, validator.validate(film).size());
    }

    @Test
    void shouldFailWhenReleaseDateBefore1895() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertEquals(1, validator.validate(film).size());
    }

    @Test
    void shouldFailWhenDurationIsNegative() {
        film.setDuration(Duration.ofMinutes(-120));
        assertEquals(1, validator.validate(film).size());
    }

    @Test
    void shouldSetIdCorrectly() {
        film.setId(5);
        assertEquals(5, film.getId());
    }
}
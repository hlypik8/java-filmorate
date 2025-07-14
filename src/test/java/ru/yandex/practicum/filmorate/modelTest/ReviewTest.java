package ru.yandex.practicum.filmorate.modelTest;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Review;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {
    private Validator validator;
    private Review review;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        review = new Review();
        review.setContent("Great film with amazing actors");
        review.setIsPositive(true);
        review.setUserId(1);
        review.setFilmId(1);
    }

    @Test
    void shouldCreateValidReview() {
        assertTrue(validator.validate(review).isEmpty(),
                "Валидный отзыв не должен иметь ошибок валидации");
    }

    @Test
    void shouldFailWhenContentIsBlank() {
        review.setContent(" ");
        assertEquals(1, validator.validate(review).size(),
                "Отзыв с пустым содержанием должен быть невалидным");
    }

    @Test
    void shouldFailWhenContentIsNull() {
        review.setContent(null);
        assertEquals(1, validator.validate(review).size(),
                "Отзыв с null содержанием должен быть невалидным");
    }

    @Test
    void shouldFailWhenIsPositiveIsNull() {
        review.setIsPositive(null);
        assertEquals(1, validator.validate(review).size(),
                "Отзыв без указания типа (позитивный/негативный) должен быть невалидным");
    }

    @Test
    void shouldAcceptZeroUserId() {
        review.setUserId(0);
        assertTrue(validator.validate(review).isEmpty(),
                "ID пользователя может быть 0, так как нет валидации на положительность");
    }

    @Test
    void shouldAcceptNegativeFilmId() {
        review.setFilmId(-1);
        assertTrue(validator.validate(review).isEmpty(),
                "ID фильма может быть отрицательным, так как нет валидации на положительность");
    }

    @Test
    void shouldSetDefaultUsefulValue() {
        assertEquals(0, review.getUseful(),
                "По умолчанию рейтинг полезности должен быть 0");
    }
}
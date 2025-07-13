package ru.yandex.practicum.filmorate.modelTest;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Review;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {
    private Validator validator;
    private Review review;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        review = new Review();
        review.setContent("Valid content");
        review.setIsPositive(true);
        review.setUserId(1);
        review.setFilmId(1);
    }

    @Test
    void shouldCreateValidReview() {
        assertTrue(validator.validate(review).isEmpty());
    }

    @Test
    void shouldFailWhenContentIsBlank() {
        review.setContent(" ");
        assertEquals(1, validator.validate(review).size());
    }

    @Test
    void shouldFailWhenIsPositiveIsNull() {
        review.setIsPositive(null);
        assertEquals(1, validator.validate(review).size());
    }

    @Test
    void shouldFailWhenUserIdIsNull() {
        review.setUserId(null);
        assertEquals(1, validator.validate(review).size());
    }

    @Test
    void shouldFailWhenFilmIdIsNull() {
        review.setFilmId(null);
        assertEquals(1, validator.validate(review).size());
    }
}
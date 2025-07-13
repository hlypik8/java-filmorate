package ru.yandex.practicum.filmorate.storageTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.reviewStorage.ReviewDbStorage;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ReviewDbStorageTest {
    private final ReviewDbStorage reviewStorage;
    private final JdbcTemplate jdbcTemplate;

    private Review testReview;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM review_ratings");
        jdbcTemplate.update("DELETE FROM reviews");
        jdbcTemplate.execute("ALTER TABLE reviews ALTER COLUMN review_id RESTART WITH 1");

        testReview = new Review();
        testReview.setContent("Test review content");
        testReview.setIsPositive(true);
        testReview.setUserId(1);
        testReview.setFilmId(1);
    }

    @Test
    void shouldCreateAndRetrieveReview() {
        Review created = reviewStorage.addReview(testReview);
        assertNotNull(created.getReviewId(), "ID отзыва не должно быть null после создания");

        Review retrieved = reviewStorage.getReviewById(created.getReviewId());
        assertEquals(created.getReviewId(), retrieved.getReviewId(), "ID полученного отзыва должно совпадать");
        assertEquals(testReview.getContent(), retrieved.getContent(), "Содержимое отзыва должно совпадать");
        assertEquals(0, retrieved.getUseful(), "Рейтинг полезности по умолчанию должен быть 0");
    }

    @Test
    void shouldUpdateReview() {
        Review created = reviewStorage.addReview(testReview);
        created.setContent("Updated content");
        created.setIsPositive(false);

        Review updated = reviewStorage.updateReview(created);
        assertEquals(created.getReviewId(), updated.getReviewId(), "ID отзыва должно остаться прежним");
        assertEquals("Updated content", updated.getContent(), "Содержимое должно обновиться");
        assertFalse(updated.getIsPositive(), "Тип отзыва должен измениться на негативный");
    }

    @Test
    void shouldThrowWhenReviewNotFound() {
        assertThrows(ReviewNotFoundException.class, () -> reviewStorage.getReviewById(999),
                "Должно выбрасываться исключение при поиске несуществующего отзыва");
    }

    @Test
    void shouldDeleteReview() {
        Review created = reviewStorage.addReview(testReview);
        reviewStorage.deleteReview(created.getReviewId());

        assertThrows(ReviewNotFoundException.class, () -> reviewStorage.getReviewById(created.getReviewId()),
                "Отзыв должен удаляться из базы данных");
    }

    @Test
    void shouldGetReviewsByFilmId() {
        reviewStorage.addReview(testReview);

        // Второй отзыв для того же фильма
        Review anotherReview = new Review();
        anotherReview.setContent("Another review");
        anotherReview.setIsPositive(false);
        anotherReview.setUserId(2);
        anotherReview.setFilmId(1);
        reviewStorage.addReview(anotherReview);

        // Отзыв для другого фильма
        Review otherFilmReview = new Review();
        otherFilmReview.setContent("Other film review");
        otherFilmReview.setIsPositive(true);
        otherFilmReview.setUserId(1);
        otherFilmReview.setFilmId(2);
        reviewStorage.addReview(otherFilmReview);

        Collection<Review> reviews = reviewStorage.getReviewsByFilmId(1, 10);
        assertEquals(2, reviews.size(), "Должны вернуться только отзывы для указанного фильма");
    }

    @Test
    void shouldManageReviewRatings() {
        Review created = reviewStorage.addReview(testReview);

        // Добавляем лайк
        reviewStorage.addRating(created.getReviewId(), 1, true);
        Review afterLike = reviewStorage.getReviewById(created.getReviewId());
        assertEquals(1, afterLike.getUseful(), "Рейтинг должен увеличиться на 1 после лайка");

        // Добавляем дизлайк от другого пользователя
        reviewStorage.addRating(created.getReviewId(), 2, false);
        Review afterDislike = reviewStorage.getReviewById(created.getReviewId());
        assertEquals(0, afterDislike.getUseful(), "Рейтинг должен быть 0 (1 лайк + 1 дизлайк)");

        // Удаляем лайк
        reviewStorage.removeRating(created.getReviewId(), 1);
        Review afterRemove = reviewStorage.getReviewById(created.getReviewId());
        assertEquals(-1, afterRemove.getUseful(), "Рейтинг должен быть -1 после удаления лайка");
    }

    @Test
    void shouldGetAllReviewsWhenFilmIdNotSpecified() {
        reviewStorage.addReview(testReview);

        Review anotherReview = new Review();
        anotherReview.setContent("Another review");
        anotherReview.setIsPositive(false);
        anotherReview.setUserId(2);
        anotherReview.setFilmId(2);
        reviewStorage.addReview(anotherReview);

        List<Review> reviews = reviewStorage.getAllReviews(10);
        assertEquals(2, reviews.size(), "Должны вернуться все отзывы");
    }
}
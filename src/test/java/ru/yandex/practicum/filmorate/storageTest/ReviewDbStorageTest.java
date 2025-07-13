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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbStorageTest {
    private final ReviewDbStorage reviewStorage;
    private final JdbcTemplate jdbcTemplate;

    private Review review;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM review_likes");
        jdbcTemplate.update("DELETE FROM reviews");
        jdbcTemplate.execute("ALTER TABLE reviews ALTER COLUMN review_id RESTART WITH 1");

        review = new Review();
        review.setContent("Great film!");
        review.setIsPositive(true);
        review.setUserId(1);
        review.setFilmId(1);
    }

    @Test
    void testCreateAndGetById() {
        Review created = reviewStorage.create(review);
        assertNotNull(created.getReviewId());

        Review found = reviewStorage.getById(created.getReviewId());
        assertEquals(created.getContent(), found.getContent());
    }

    @Test
    void testUpdate() {
        Review created = reviewStorage.create(review);
        created.setContent("Updated content");
        created.setIsPositive(false);

        Review updated = reviewStorage.update(created);
        assertEquals("Updated content", updated.getContent());
        assertFalse(updated.getIsPositive());
    }

    @Test
    void testDelete() {
        Review created = reviewStorage.create(review);
        reviewStorage.delete(created.getReviewId());

        assertThrows(ReviewNotFoundException.class, () -> reviewStorage.getById(created.getReviewId()));
    }

    @Test
    void testGetByFilmId() {
        reviewStorage.create(review);
        Review review2 = new Review();
        review2.setContent("Bad film!");
        review2.setIsPositive(false);
        review2.setUserId(2);
        review2.setFilmId(1);
        reviewStorage.create(review2);

        Collection<Review> reviews = reviewStorage.getByFilmId(1, 10);
        assertEquals(2, reviews.size());
    }

    @Test
    void testLikeDislike() {
        Review created = reviewStorage.create(review);

        reviewStorage.addLike(created.getReviewId(), 1);
        Review afterLike = reviewStorage.getById(created.getReviewId());
        assertEquals(1, afterLike.getUseful());

        reviewStorage.removeLike(created.getReviewId(), 1);
        Review afterRemove = reviewStorage.getById(created.getReviewId());
        assertEquals(0, afterRemove.getUseful());

        reviewStorage.addDislike(created.getReviewId(), 1);
        Review afterDislike = reviewStorage.getById(created.getReviewId());
        assertEquals(-1, afterDislike.getUseful());
    }
}
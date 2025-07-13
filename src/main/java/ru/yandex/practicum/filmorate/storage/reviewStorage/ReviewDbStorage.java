package ru.yandex.practicum.filmorate.storage.reviewStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewRowMapper;

import java.util.List;

@Repository
public class ReviewDbStorage extends BaseStorage<Review> {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewRowMapper reviewRowMapper;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate, ReviewRowMapper reviewRowMapper) {
        super(jdbcTemplate);
        this.jdbcTemplate = jdbcTemplate;
        this.reviewRowMapper = reviewRowMapper;
    }

    public Review addReview(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");

        int id = simpleJdbcInsert.executeAndReturnKey(review.toMap()).intValue();
        review.setReviewId(id);
        return review;
    }

    public Review updateReview(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        int updated = jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());

        if (updated == 0) {
            throw new NotFoundException("Отзыв не найден");
        }
        return getReviewById(review.getReviewId());
    }

    public void deleteReview(int id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        if (!delete(sql, id)) {
            throw new NotFoundException("Отзыв не найден");
        }
    }

    public Review getReviewById(int id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        Review review = findOne(sql, reviewRowMapper, id);
        if (review == null) {
            throw new NotFoundException("Отзыв не найден");
        }
        return review;
    }

    public List<Review> getReviewsByFilmId(int filmId, int count) {
        String sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        return findMany(sql, reviewRowMapper, filmId, count);
    }

    public List<Review> getAllReviews(int count) {
        String sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
        return findMany(sql, reviewRowMapper, count);
    }

    public void addRating(int reviewId, int userId, boolean isLike) {
        String sql = "MERGE INTO review_ratings (review_id, user_id, is_positive) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, isLike);
        updateUseful(reviewId);
    }

    public void removeRating(int reviewId, int userId) {
        String sql = "DELETE FROM review_ratings WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, reviewId, userId);
        updateUseful(reviewId);
    }

    private void updateUseful(int reviewId) {
        String sql = "UPDATE reviews SET useful = " +
                "(SELECT SUM(CASE WHEN is_positive THEN 1 ELSE -1 END) FROM review_ratings WHERE review_id = ?) " +
                "WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewId, reviewId);
    }
}
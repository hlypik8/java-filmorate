package ru.yandex.practicum.filmorate.storage.reviewStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.storage.userStorage.UserDbStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewRowMapper reviewRowMapper;
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    public Review addReview(Review review) {
        checkUserExists(review.getUserId());
        checkFilmExists(review.getFilmId());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("content", review.getContent());
        parameters.put("is_positive", review.getIsPositive());
        parameters.put("user_id", review.getUserId());
        parameters.put("film_id", review.getFilmId());
        parameters.put("useful", 0);

        int id = simpleJdbcInsert.executeAndReturnKey(parameters).intValue();
        review.setReviewId(id);
        return review;
    }

    public Review updateReview(Review review) {
        checkReviewExists(review.getReviewId());

        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        int updated = jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        if (updated == 0) {
            throw new NotFoundException("Review not found");
        }
        return getReviewById(review.getReviewId());
    }

    public void deleteReview(int id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        if (jdbcTemplate.update(sql, id) == 0) {
            throw new NotFoundException("Review not found");
        }
    }

    public Review getReviewById(int id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, reviewRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Review not found");
        }
    }

    public List<Review> getReviewsByFilmId(Integer filmId, int count) {
        if (filmId != null) {
            checkFilmExists(filmId);
            String sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
            return jdbcTemplate.query(sql, reviewRowMapper, filmId, count);
        }
        String sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, reviewRowMapper, count);
    }

    public void addLike(int reviewId, int userId) {
        checkReviewExists(reviewId);
        checkUserExists(userId);

        String sql = "MERGE INTO review_ratings (review_id, user_id, is_positive) VALUES (?, ?, true)";
        jdbcTemplate.update(sql, reviewId, userId);
        updateUseful(reviewId);
    }

    public void addDislike(int reviewId, int userId) {
        checkReviewExists(reviewId);
        checkUserExists(userId);

        String sql = "MERGE INTO review_ratings (review_id, user_id, is_positive) VALUES (?, ?, false)";
        jdbcTemplate.update(sql, reviewId, userId);
        updateUseful(reviewId);
    }

    public void removeLike(int reviewId, int userId) {
        String sql = "DELETE FROM review_ratings WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, reviewId, userId);
        updateUseful(reviewId);
    }

    private void updateUseful(int reviewId) {
        String sql = "UPDATE reviews SET useful = " +
                "(SELECT COALESCE(SUM(CASE WHEN is_positive THEN 1 ELSE -1 END), 0) " +
                "FROM review_ratings WHERE review_id = ? " +
                "WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewId, reviewId);
    }

    private void checkReviewExists(int reviewId) {
        if (!reviewExists(reviewId)) {
            throw new NotFoundException("Review not found");
        }
    }

    private void checkUserExists(int userId) {
        if (!userExists(userId)) {
            throw new NotFoundException("User not found");
        }
    }

    private void checkFilmExists(int filmId) {
        if (!filmExists(filmId)) {
            throw new NotFoundException("Film not found");
        }
    }

    private boolean reviewExists(int reviewId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE review_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, reviewId) > 0;
    }

    private boolean userExists(int userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId) > 0;
    }

    private boolean filmExists(int filmId) {
        String sql = "SELECT COUNT(*) FROM films WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, filmId) > 0;
    }
}
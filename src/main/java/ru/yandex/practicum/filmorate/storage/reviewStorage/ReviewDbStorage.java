package ru.yandex.practicum.filmorate.storage.reviewStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewRowMapper;

import java.util.Collection;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage extends BaseStorage<Review> implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewRowMapper reviewRowMapper;

    @Override
    public Review create(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");

        Map<String, Object> values = Map.of(
                "content", review.getContent(),
                "is_positive", review.getIsPositive(),
                "user_id", review.getUserId(),
                "film_id", review.getFilmId(),
                "useful", review.getUseful(),
                "created_at", review.getCreatedAt()
        );

        int id = simpleJdbcInsert.executeAndReturnKey(values).intValue();
        review.setReviewId(id);
        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";

        int updated = jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        if (updated == 0) {
            throw new ReviewNotFoundException("Отзыв с id=" + review.getReviewId() + " не найден");
        }

        return getById(review.getReviewId());
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        if (!delete(sql, id)) {
            throw new ReviewNotFoundException("Отзыв с id=" + id + " не найден");
        }
    }

    @Override
    public Review getById(int id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        return findOne(sql, reviewRowMapper, id)
                .orElseThrow(() -> new ReviewNotFoundException("Отзыв с id=" + id + " не найден"));
    }

    @Override
    public Collection<Review> getByFilmId(Integer filmId, int count) {
        String sql;
        Object[] params;

        if (filmId == null) {
            sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
            params = new Object[]{count};
        } else {
            sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
            params = new Object[]{filmId, count};
        }

        return findMany(sql, reviewRowMapper, params);
    }

    @Override
    public void addLike(int reviewId, int userId) {
        String sql = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, TRUE)";
        jdbcTemplate.update(sql, reviewId, userId);
        updateUseful(reviewId, 1);
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        String sql = "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, FALSE)";
        jdbcTemplate.update(sql, reviewId, userId);
        updateUseful(reviewId, -1);
    }

    @Override
    public void removeLike(int reviewId, int userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = TRUE";
        if (jdbcTemplate.update(sql, reviewId, userId) > 0) {
            updateUseful(reviewId, -1);
        }
    }

    @Override
    public void removeDislike(int reviewId, int userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = FALSE";
        if (jdbcTemplate.update(sql, reviewId, userId) > 0) {
            updateUseful(reviewId, 1);
        }
    }

    private void updateUseful(int reviewId, int delta) {
        String sql = "UPDATE reviews SET useful = useful + ? WHERE review_id = ?";
        jdbcTemplate.update(sql, delta, reviewId);
    }
}
package ru.yandex.practicum.filmorate.storage.reviewStorage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.storage.userStorage.UserDbStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ReviewDbStorage extends BaseStorage<Review> {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewRowMapper reviewRowMapper;
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate,
                           ReviewRowMapper reviewRowMapper,
                           UserDbStorage userStorage,
                           FilmDbStorage filmStorage) {
        super(jdbcTemplate);
        this.jdbcTemplate = jdbcTemplate;
        this.reviewRowMapper = reviewRowMapper;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Review addReview(Review review) {
        // Проверка существования пользователя
        try {
            userStorage.getUserById(review.getUserId());
        } catch (NotFoundException e) {
            throw new NotFoundException("Пользователь с id=" + review.getUserId() + " не найден");
        }

        // Проверка существования фильма
        try {
            filmStorage.getFilmById(review.getFilmId());
        } catch (NotFoundException e) {
            throw new NotFoundException("Фильм с id=" + review.getFilmId() + " не найден");
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("content", review.getContent());
        parameters.put("is_positive", review.getIsPositive());
        parameters.put("user_id", review.getUserId());
        parameters.put("film_id", review.getFilmId());
        parameters.put("useful", 0); // Начальное значение полезности

        int reviewId = simpleJdbcInsert.executeAndReturnKey(parameters).intValue();
        review.setReviewId(reviewId);
        review.setUseful(0);
        return review;
    }

    public Review updateReview(Review review) {
        // Проверка существования отзыва
        if (!reviewExists(review.getReviewId())) {
            throw new NotFoundException("Отзыв с id=" + review.getReviewId() + " не найден");
        }

        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        int updatedRows = jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        if (updatedRows == 0) {
            throw new NotFoundException("Отзыв с id=" + review.getReviewId() + " не найден");
        }

        return getReviewById(review.getReviewId());
    }

    public void deleteReview(int id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        int deletedRows = jdbcTemplate.update(sql, id);

        if (deletedRows == 0) {
            throw new NotFoundException("Отзыв с id=" + id + " не найден");
        }
    }

    public Review getReviewById(int id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, reviewRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Отзыв с id=" + id + " не найден");
        }
    }

    public List<Review> getReviewsByFilmId(Integer filmId, int count) {
        if (filmId != null) {
            // Проверка существования фильма
            try {
                filmStorage.getFilmById(filmId);
            } catch (NotFoundException e) {
                throw new NotFoundException("Фильм с id=" + filmId + " не найден");
            }

            String sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
            return jdbcTemplate.query(sql, reviewRowMapper, filmId, count);
        } else {
            String sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
            return jdbcTemplate.query(sql, reviewRowMapper, count);
        }
    }

    public List<Review> getAllReviews(int count) {
        String sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, reviewRowMapper, count);
    }

    public void addRating(int reviewId, int userId, boolean isLike) {
        // Проверка существования отзыва и пользователя
        if (!reviewExists(reviewId)) {
            throw new NotFoundException("Отзыв с id=" + reviewId + " не найден");
        }
        if (!userExists(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }

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
                "(SELECT COALESCE(SUM(CASE WHEN is_positive THEN 1 ELSE -1 END), 0) " +
                "FROM review_ratings WHERE review_id = ?) " +
                "WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewId, reviewId);
    }

    private boolean reviewExists(int reviewId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE review_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, reviewId);
        return count != null && count > 0;
    }

    private boolean userExists(int userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }

    private boolean filmExists(int filmId) {
        String sql = "SELECT COUNT(*) FROM films WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filmId);
        return count != null && count > 0;
    }
}
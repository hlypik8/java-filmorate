package ru.yandex.practicum.filmorate.storage.reviewStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.storage.userStorage.UserDbStorage;

import java.util.Collection;
import java.util.List;

@Repository
public class ReviewDbStorage extends BaseStorage<Review> {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewRowMapper reviewRowMapper;
    private final FilmDbStorage filmStorage;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate,
                           ReviewRowMapper reviewRowMapper,
                           UserDbStorage userStorage,
                           FilmDbStorage filmStorage) {
        super(jdbcTemplate);
        this.jdbcTemplate = jdbcTemplate;
        this.reviewRowMapper = reviewRowMapper;
        this.filmStorage = filmStorage;
    }

    public Review addReview(Review review) {

        String query = """
                INSERT INTO reviews (content, is_positive, user_id, film_id, useful)
                VALUES (?,?,?,?,?);
                """;

        int id = insert(query,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                0);

        review.setReviewId(id);

        return review;
    }

    public Review updateReview(Review review) {

        String query = """
                UPDATE reviews
                SET content = ?, is_positive = ?
                WHERE review_id = ?
                """;

        update(query,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        return getReviewById(review.getReviewId());
    }

    public void deleteReview(int id) {

        String query = """
                DELETE FROM reviews
                WHERE review_id = ?;
                """;

        delete(query, id);
    }

    public Review getReviewById(int id) {

        String query = """
                SELECT *
                FROM reviews
                WHERE review_id = ?;
                """;

        return findOne(query, reviewRowMapper, id);
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

    public Collection<Review> getAllReviews(int count) {
        String query = """
                SELECT *
                FROM reviews
                ORDER BY useful DESC
                LIMIT ?;
                """;

        return findMany(query, reviewRowMapper, count);
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